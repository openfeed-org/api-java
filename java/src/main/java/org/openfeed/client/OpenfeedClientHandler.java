package org.openfeed.client;

import org.openfeed.HeartBeat;
import org.openfeed.InstrumentDefinition;
import org.openfeed.InstrumentReferenceResponse;
import org.openfeed.InstrumentResponse;
import org.openfeed.LoginResponse;
import org.openfeed.LogoutResponse;
import org.openfeed.MarketSnapshot;
import org.openfeed.MarketStatus;
import org.openfeed.MarketUpdate;
import org.openfeed.Ohlc;
import org.openfeed.SubscriptionResponse;
import org.openfeed.VolumeAtPrice;

import io.netty.channel.ChannelPromise;

public interface OpenfeedClientHandler {
    // Request Responses
    void onLoginResponse(LoginResponse loginResponse);
    void onLogoutResponse(LogoutResponse logoutResponse);
    void onInstrumentResponse(InstrumentResponse instrumentResponse);
    void onInstrumentReferenceResponse(InstrumentReferenceResponse instrumentReferenceResponse);
    // Promise to trigger end of instrument and/or instrument reference downloads
    void setInstrumentPromise(ChannelPromise promise);
    void onSubscriptionResponse(SubscriptionResponse subscriptionResponse);

    // Streaming
    void onMarketStatus(MarketStatus marketStatus);
    void onHeartBeat(HeartBeat hb);
    void onInstrumentDefinition(InstrumentDefinition definition);
    void onMarketSnapshot(MarketSnapshot snapshot);
    void onMarketUpdate(MarketUpdate update);
    void onVolumeAtPrice(VolumeAtPrice cumulativeVolume);
    void onOhlc(Ohlc ohlc);

    ConnectionStats getConnectionStats();

}
