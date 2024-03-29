package site.termterm.api.global.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackChannelConfig {
    @Value("${slack.channel.emergency}")
    private String CHANNEL_EMERGENCY;

    @Value("${slack.channel.info}")
    private String CHANNEL_INFO;

    @Value("${slack.channel.voc}")
    private String CHANNEL_VOC;

    @Bean
    public SlackChannels slackChannels(){
        return SlackChannels.builder()
                .CHANNEL_EMERGENCY(CHANNEL_EMERGENCY)
                .CHANNEL_INFO(CHANNEL_INFO)
                .CHANNEL_VOC(CHANNEL_VOC)
                .build();
    }
}
