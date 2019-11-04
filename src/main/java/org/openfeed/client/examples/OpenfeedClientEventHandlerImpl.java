package org.openfeed.client.examples;

import org.openfeed.InstrumentDefinition;
import org.openfeed.Service;
import org.openfeed.client.api.*;
import org.openfeed.client.api.impl.ConnectionStats;
import org.openfeed.client.api.impl.websocket.OpenfeedClientWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OpenfeedClientEventHandlerImpl implements OpenfeedClientEventHandler {
    private static final Logger log = LoggerFactory.getLogger(OpenfeedClientEventHandler.class);

    private final String clientId;
    private final OpenfeedClientConfig config;
    private final InstrumentCache instrumentCache;
    private final ConnectionStats connectionStats;

    public OpenfeedClientEventHandlerImpl(OpenfeedClientConfig config, InstrumentCache instrumentCache, ConnectionStats stats) {
        this.clientId = config.getClientId();
        this.config = config;
        this.instrumentCache = instrumentCache;
        this.connectionStats = stats;
    }

    @Override
    public void onEvent(OpenfeedClient client, OpenfeedEvent event) {
        log.info("{}: Event: {} - {}", config.getClientId(), event.getType(), event.getMessage());
        switch (event.getType()) {
            case Connected:
                if (config.getStatsDisplaySeconds() > 0) {
                    client.scheduleAtFixedRate(() -> {
                        log.info("{}: connected: {} {}", this.clientId, client.isConnected(), this.connectionStats);
                    }, 5, config.getStatsDisplaySeconds(), TimeUnit.SECONDS);
                }
                break;
            case Login:
                executeCommands(client);
                break;
            case Disconnected:
            case Logout:
            default:
                break;
        }
    }


    private void executeCommands(OpenfeedClient client) {
        if(client.isReConnect()) {
            // The client will re-subscribe on re-connect.
            return;
        }

        if (config.isInstrumentRequest()) {
            if (config.getSymbols() != null) {
                client.instrument(config.getSymbols());
            }
            if (config.getMarketIds() != null) {
                client.instrumentMarketId(config.getMarketIds());
            }
            if (config.getExchanges() != null) {
                for (String e : config.getExchanges()) {
                    client.instrumentExchange(e);
                }
            }
            if (config.getChannelIds() != null) {
                for (int chId : config.getChannelIds()) {
                    client.instrumentChannel(chId);
                }
            }
        } else if (config.isInstrumentCrossReferenceRequest()) {
            if (config.getSymbols() != null) {
                client.instrumentReference(config.getSymbols());
            }
            if (config.getMarketIds() != null) {
                client.instrumentReferenceMarketId(config.getMarketIds());
            }
            if (config.getExchanges() != null) {
                for (String exchange : config.getExchanges()) {
                    client.instrumentReferenceExchange(exchange);
                }
            }
            if (config.getChannelIds() != null) {
                for (int channelId : config.getChannelIds()) {
                    client.instrumentReferenceChannel(channelId);
                }
            }
        } else if (config.getSymbols() != null) {
            client.subscribe(Service.REAL_TIME, config.getSubcriptionType(), config.getSymbols());
        } else if (config.getMarketIds() != null) {
            client.subscribe(Service.REAL_TIME, config.getSubcriptionType(), config.getMarketIds());
        } else if (config.getExchanges() != null && config.getExchanges().length > 0) {
            client.subscribeExchange(Service.REAL_TIME, config.getSubcriptionType(), config.getExchanges());
        } else if (config.getChannelIds() != null && config.getChannelIds().length > 0) {
            // Subscribe
            client.subscribeChannel(Service.REAL_TIME, config.getSubcriptionType(), config.getChannelIds());
        }
    }
}
