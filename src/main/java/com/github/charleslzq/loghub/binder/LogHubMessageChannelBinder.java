package com.github.charleslzq.loghub.binder;

import com.aliyun.openservices.loghub.client.config.LogHubConfig;
import com.github.charleslzq.integration.loghub.LogHubMessageDrivenChannelAdapter;
import com.github.charleslzq.loghub.binder.config.*;
import com.github.charleslzq.loghub.binder.extract.SpansExtractor;
import com.github.charleslzq.loghub.config.LogConsumerConfig;
import com.github.charleslzq.loghub.config.LogHubProducerProperties;
import com.github.charleslzq.loghub.config.LogHubProjectConfig;
import com.github.charleslzq.loghub.config.SourceType;
import com.github.charleslzq.loghub.converter.LogItemConverter;
import com.github.charleslzq.loghub.listener.ClientWorkerContainer;
import com.github.charleslzq.loghub.producer.LogHubProducerFactory;
import com.github.charleslzq.loghub.producer.LogHubTemplate;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Charles on 3/1/2017.
 */
public class LogHubMessageChannelBinder
		extends AbstractMessageChannelBinder<
		ExtendedConsumerProperties<LogHubConsumerConfig>,
		ExtendedProducerProperties<LogHubProducerConfig>,
		ConsumerDestination,
		ProducerDestination>
		implements ExtendedPropertiesBinder<
		MessageChannel,
		LogHubConsumerConfig,
		LogHubProducerConfig> {
	private final LogHubExtendedBindingProperties logHubExtendedBindingProperties;
	private final SpansExtractor spansExtractor;
	private LogHubProducerFactory producerFactory;
	private String hostIp = "127.0.0.1";
	private String hostName = "localhost";

	public LogHubMessageChannelBinder(LogHubExtendedBindingProperties logHubExtendedBindingProperties, SpansExtractor spansExtractor) {
		super(true, new String[]{});
		this.logHubExtendedBindingProperties = logHubExtendedBindingProperties;
		this.spansExtractor = spansExtractor;
	}

	@Override
	protected void onInit() {
		initProducerFactory();
		try {
			hostIp = InetAddress.getLocalHost().getHostAddress();
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void initProducerFactory() {
		LogHubProducerProperties producerProperties = new LogHubProducerProperties();
		producerProperties.setProjects(
				new ArrayList<>(logHubExtendedBindingProperties.getProjects().values())
		);
		LogHubProducerParameter producerParameter = logHubExtendedBindingProperties.getProducerParameter();
		producerProperties.setRetry(producerParameter.getRetry());
		producerProperties.setUpdateIntervalMs(producerParameter.getUpdateIntervalMs());
		producerProperties.setMaxMemSize(producerParameter.getMaxMemSize());
		producerProperties.setMaxIOThread(producerParameter.getMaxIOThread());
		producerProperties.setBytesPerPackage(producerParameter.getBytesPerPackage());
		producerProperties.setLogPerPackage(producerParameter.getLogPerPackage());
		producerProperties.setPackageTimeOutMs(producerParameter.getPackageTimeOutMs());

		producerFactory = new LogHubProducerFactory(producerProperties);
	}

	private LogConsumerConfig generateConsumerProperty(String destination, LogHubConsumerConfig consumerConfig) {
		LogHubProjectConfig projectConfig = logHubExtendedBindingProperties.getProjects().get(destination);
		Assert.isTrue(projectConfig != null,
				"Can't find destination project for consumer " + destination);
		LogConsumerConfig logConsumerConfig = new LogConsumerConfig();
		logConsumerConfig.setEndpoint(projectConfig.getEndpoint());
		logConsumerConfig.setProject(projectConfig.getProject());
		logConsumerConfig.setAccessId(projectConfig.getAccessId());
		logConsumerConfig.setAccessKey(projectConfig.getAccessKey());
		logConsumerConfig.setStore(consumerConfig.getStore());
		logConsumerConfig.setGroupName(consumerConfig.getGroup());
		logConsumerConfig.setCursorPosition(consumerConfig.getCursorPosition());
		logConsumerConfig.setStartTime(consumerConfig.getStartTime());
		logConsumerConfig.setFetchIntervalMillis(consumerConfig.getFetchIntervalMillis());
		logConsumerConfig.setHeartBeatIntervalMillis(consumerConfig.getHeartBeatIntervalMillis());
		logConsumerConfig.setKeepOrder(consumerConfig.isKeepOrder());
		return logConsumerConfig;
	}

	@Override
	protected ProducerDestination createProducerDestinationIfNecessary(
			String destination,
			ExtendedProducerProperties<LogHubProducerConfig> logHubProducerConfigExtendedProducerProperties) {
		return new ProducerDestination(logHubExtendedBindingProperties.getProjects().get(destination));
	}

	@Override
	protected MessageHandler createProducerMessageHandler(
			ProducerDestination producerDestination,
			ExtendedProducerProperties<LogHubProducerConfig> logHubProducerConfigExtendedProducerProperties) throws Exception {
		LogHubProducerConfig producerConfig = logHubProducerConfigExtendedProducerProperties.getExtension();
		LogItemConverter converter = (LogItemConverter) Class.forName(producerConfig.getConverter()).newInstance();
		LogHubTemplate logHubTemplate = producerFactory.createTemplate(
				producerDestination.getProject().getProject(),
				producerConfig.getStore(),
				producerConfig.getSource() == SourceType.HOST_IP ? hostIp : hostName,
				producerConfig.getTopic(),
				converter
		);
		return message -> logHubTemplate.send(Collections.singletonList(message.getPayload()));
	}

	@Override
	protected ConsumerDestination createConsumerDestinationIfNecessary(
			String destination,
			String group, ExtendedConsumerProperties<LogHubConsumerConfig> logHubConsumerConfigExtendedConsumerProperties) {
		return new ConsumerDestination(logHubExtendedBindingProperties.getProjects().get(destination));
	}

	@Override
	protected MessageProducer createConsumerEndpoint(
			String destination,
			String group,
			ConsumerDestination consumerDestination,
			ExtendedConsumerProperties<LogHubConsumerConfig> logHubConsumerConfigExtendedConsumerProperties) {
		LogConsumerConfig logConsumerConfig = generateConsumerProperty(destination, logHubConsumerConfigExtendedConsumerProperties.getExtension());
		LogHubConfig logHubConfig = logConsumerConfig.generateLogHubConfig(destination + "$" + group + System.currentTimeMillis());
		ClientWorkerContainer container = new ClientWorkerContainer(
				spansExtractor,
				new SimpleAsyncTaskExecutor(),
				logHubConfig
		);
		return new LogHubMessageDrivenChannelAdapter(container);
	}

	@Override
	public LogHubConsumerConfig getExtendedConsumerProperties(String channelName) {
		return logHubExtendedBindingProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public LogHubProducerConfig getExtendedProducerProperties(String channelName) {
		return logHubExtendedBindingProperties.getExtendedProducerProperties(channelName);
	}
}
