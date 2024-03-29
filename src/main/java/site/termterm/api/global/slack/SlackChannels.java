package site.termterm.api.global.slack;

import lombok.Builder;

@Builder
public class SlackChannels {
    public String CHANNEL_EMERGENCY;
    public String CHANNEL_INFO;
    public String CHANNEL_VOC;
}