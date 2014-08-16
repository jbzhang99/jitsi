/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.irc;

import net.java.sip.communicator.util.*;

import com.ircclouds.irc.api.*;
import com.ircclouds.irc.api.domain.messages.*;
import com.ircclouds.irc.api.listeners.*;
import com.ircclouds.irc.api.state.*;

/**
 * Manager for presence status of IRC connection.
 *
 * @author Danny van Heumen
 */
public class PresenceManager
{
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger
        .getLogger(PresenceManager.class);

    /**
     * IRC client library instance.
     */
    private final IRCApi irc;

    /**
     * IRC client connection state.
     */
    private final IIRCState state;

    /**
     * Instance of OperationSetPersistentPresence for updates.
     */
    private final OperationSetPersistentPresenceIrcImpl operationSet;

    /**
     * Current presence status.
     */
    private volatile boolean away = false;

    /**
     * Active away message.
     */
    private String currentMessage;

    /**
     * Proposed away message.
     */
    private String submittedMessage;

    /**
     * Constructor.
     *
     * @param irc irc client library instance
     * @param state irc client connection state instance
     * @param operationSet OperationSetPersistentPresence irc implementation for
     *            handling presence changes.
     */
    public PresenceManager(final IRCApi irc, final IIRCState state,
        final OperationSetPersistentPresenceIrcImpl operationSet)
    {
        if (state == null)
        {
            throw new IllegalArgumentException("state cannot be null");
        }
        this.state = state;
        if (operationSet == null)
        {
            throw new IllegalArgumentException("operationSet cannot be null");
        }
        this.operationSet = operationSet;
        if (irc == null)
        {
            throw new IllegalArgumentException("irc cannot be null");
        }
        irc.addListener(new PresenceListener());
        this.irc = irc;
    }

    /**
     * Check current Away state.
     *
     * @return returns <tt>true</tt> if away or <tt>false</tt> if not away
     */
    public boolean isAway()
    {
        return this.away;
    }

    /**
     * Get away message.
     *
     * @return returns currently active away message or "" if currently not
     *         away.
     */
    public String getMessage()
    {
        return this.away ? this.currentMessage : "";
    }

    /**
     * Set away status and message. Disable away status by providing
     * <tt>null</tt> message.
     *
     * @param awayMessage away message to set. If away message is <tt>null</tt>
     *            or empty away status will be cancelled.
     */
    public void setAway(final String awayMessage)
    {
        if (awayMessage == null || awayMessage.isEmpty())
        {
            this.irc.rawMessage("AWAY");
        }
        else
        {
            this.submittedMessage = awayMessage;
            this.irc.rawMessage("AWAY :" + awayMessage);
        }
    }

    /**
     * Presence listener implementation for keeping track of presence changes in
     * the IRC connection.
     *
     * @author Danny van Heumen
     */
    private final class PresenceListener
        extends VariousMessageListenerAdapter
    {
        /**
         * Reply for acknowledging transition to available (not away any
         * longer).
         */
        private static final int IRC_RPL_UNAWAY = 305;

        /**
         * Reply for acknowledging transition to away.
         */
        private static final int IRC_RPL_NOWAWAY = 306;

        /**
         * Presence listener constructor.
         */
        private PresenceListener()
        {
        }

        @Override
        public void onServerNumericMessage(final ServerNumericMessage msg)
        {
            Integer msgCode = msg.getNumericCode();
            if (msgCode == null)
            {
                return;
            }
            int code = msgCode.intValue();
            switch (code)
            {
            case IRC_RPL_UNAWAY:
                PresenceManager.this.currentMessage = "";
                PresenceManager.this.away = false;
                operationSet.updatePresenceStatus(IrcStatusEnum.AWAY,
                    IrcStatusEnum.ONLINE);
                LOGGER.debug("Away status disabled.");
                break;
            case IRC_RPL_NOWAWAY:
                PresenceManager.this.currentMessage =
                    PresenceManager.this.submittedMessage;
                PresenceManager.this.away = true;
                operationSet.updatePresenceStatus(IrcStatusEnum.ONLINE,
                    IrcStatusEnum.AWAY);
                LOGGER.debug("Away status enabled.");
                break;
            default:
                break;
            }
        }

        @Override
        public void onUserQuit(final QuitMessage msg)
        {
            final String user = msg.getSource().getNick();
            if (user == null
                || !user.equals(PresenceManager.this.state.getNickname()))
            {
                return;
            }
            LOGGER.debug("Local user's QUIT message received: removing "
                + "presence listener.");
            PresenceManager.this.irc.deleteListener(this);
        }
    }
}
