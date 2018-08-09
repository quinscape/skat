package de.quinscape.domainql.skat.runtime.service;

import de.quinscape.domainql.skat.domain.tables.pojos.UserConfig;
import de.quinscape.domainql.skat.domain.tables.records.UserConfigRecord;
import de.quinscape.domainql.skat.ws.SkatWebSocketHandler;
import de.quinscape.domainql.annotation.GraphQLLogic;
import de.quinscape.domainql.annotation.GraphQLMutation;
import de.quinscape.domainql.annotation.GraphQLQuery;
import de.quinscape.domainql.skat.domain.Tables;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@GraphQLLogic
public class UserLogic
{
    private final static Logger log = LoggerFactory.getLogger(UserLogic.class);

    private final DSLContext dslContext;

    private final ChannelRepository channelRepository;

    private final Random random;

    private final SkatWebSocketHandler skatWebSocketHandler;


    @Autowired
    public UserLogic(
        DSLContext dslContext,
        ChannelRepository channelRepository,
        Random random,
        SkatWebSocketHandler skatWebSocketHandler
    )
    {
        this.dslContext = dslContext;
        this.channelRepository = channelRepository;
        this.random = random;
        this.skatWebSocketHandler = skatWebSocketHandler;
    }


    @GraphQLQuery
    public UserConfig userConfig()
    {
        final AppAuthentication currentUser = AppAuthentication.current();

        final List<UserConfig> userConfigs = dslContext.select()
            .from(Tables.USER_CONFIG)
            .where(
                Tables.USER_CONFIG.USER_ID.eq(currentUser.getId())
            )
            .fetchInto(UserConfig.class);

        if (userConfigs.size() == 0)
        {
            return new UserConfig(
                "-",
                currentUser.getId(),
                true
            );
        }
        return userConfigs.get(0);
    }


    @GraphQLMutation
    public UserConfig storeUserConfig(UserConfig userConfig)
    {
        final UserConfigRecord userConfigRecord = dslContext.newRecord(Tables.USER_CONFIG);
        userConfigRecord.setUserId(userConfig.getUserId());
        userConfigRecord.setLockBidding(userConfig.getLockBidding());

        final String userConfigId = userConfig.getId();
        if (userConfigId.equals("-"))
        {
            userConfigRecord.setId(UUID.randomUUID().toString());

            log.debug("Insert {}", userConfigRecord);

            userConfigRecord.insert();
        }
        else
        {
            userConfigRecord.setId(userConfigId);
            log.debug("Update {}", userConfigRecord);
            userConfigRecord.update();
        }

        return userConfigRecord.into(UserConfig.class);
    }
}
