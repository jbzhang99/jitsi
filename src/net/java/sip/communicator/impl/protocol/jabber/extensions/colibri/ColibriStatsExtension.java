/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber.extensions.colibri;

import java.util.*;

import net.java.sip.communicator.impl.protocol.jabber.extensions.*;

import org.jivesoftware.smack.packet.*;
import org.jivesoftware.smack.util.*;

/**
 * Implements the Jitsi Videobridge <tt>stats</tt> extension within COnferencing
 * with LIghtweight BRIdging that will provide various statistics.
 *
 * @author Hristo Terezov
 */
public class ColibriStatsExtension
    extends AbstractPacketExtension
{
    /**
     * The XML element name of the Jitsi Videobridge <tt>stats</tt> extension.
     */
    public static final String ELEMENT_NAME = "stats";

    /**
     * The XML COnferencing with LIghtweight BRIdging namespace of the Jitsi
     * Videobridge <tt>stats</tt> extension.
     */
    public static final String NAMESPACE
        = "http://jitsi.org/protocol/colibri";

    /**
     * Constructs new <tt>ColibriStatsExtension</tt>
     */
    public ColibriStatsExtension()
    {
        super(NAMESPACE, ELEMENT_NAME);
    }

    /**
     * Adds stat extension.
     * @param stat the stat to be added
     */
    public void addStat(Stat stat)
    {
        addChildExtension(stat);
    }

    @Override
    public List<? extends PacketExtension> getChildExtensions()
    {
        return Collections.unmodifiableList(super.getChildExtensions());
    }

    public static class Stat
        extends AbstractPacketExtension
    {
        /**
         * The XML element name of a <tt>content</tt> of a Jitsi Videobridge
         * <tt>stats</tt> IQ.
         */
        public static final String ELEMENT_NAME = "stat";

        /**
         * The XML name of the <tt>name</tt> attribute of a <tt>stat</tt> of a
         * <tt>stats</tt> IQ which represents the <tt>name</tt> property of the
         * statistic.
         */
        public static final String NAME_ATTR_NAME = "name";

        /**
         * The XML name of the <tt>name</tt> attribute of a <tt>stat</tt> of a
         * <tt>stats</tt> IQ which represents the <tt>value</tt> property of the
         * statistic.
         */
        public static final String VALUE_ATTR_NAME = "value";

        /**
         * The name attribute value.
         */
        private String name;

        /**
         * The value attribute value.
         */
        private Object value;

        public Stat()
        {
            super(NAMESPACE, ELEMENT_NAME);
        }

        /**
         * Constructs new <tt>Stat</tt> by given name and value.
         * @param name the name
         * @param value the value
         */
        public Stat(String name, Object value)
        {
            this();
            this.setName(name);
            this.setValue(value);
        }

        @Override
        public String getElementName()
        {
            return ELEMENT_NAME;
        }

        /**
         * @return the name
         */
        public String getName()
        {
            return name;
        }

        @Override
        public String getNamespace()
        {
            return NAMESPACE;
        }

        /**
         * @return the value
         */
        public Object getValue()
        {
            return value;
        }

        /**
         * @param name the name to set
         */
        public void setName(String name)
        {
            this.name = name;
        }

        /**
         * @param value the value to set
         */
        public void setValue(Object value)
        {
            this.value = value;
        }

        @Override
        public String toXML()
        {
            String name = getName();
            Object value = getValue();

            if ((name == null) || (value == null))
            {
                return "";
            }
            else
            {
                return
                    "<" + ELEMENT_NAME + " " + NAME_ATTR_NAME + "='"
                        + StringUtils.escapeForXML(name) + "' "
                        + VALUE_ATTR_NAME + "='"
                        + StringUtils.escapeForXML(value.toString()) + "' />";
            }
        }
    }
}
