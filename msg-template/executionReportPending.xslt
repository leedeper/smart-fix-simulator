<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:java="smart.fixsimulator.fixacceptor.core.buildin"
                exclude-result-prefixes="java">
    <xsl:template match="/">
        <xsl:variable name="side" select="message/body/field[@tag='54']"/>
        <message>
            <header>
                <field tag="8"><![CDATA[FIX.4.0]]></field>
                <field tag="9"><![CDATA[168]]></field>
                <field tag="34"><![CDATA[16]]></field>
                <field tag="35"><![CDATA[8]]></field>
                <field tag="49"><![CDATA[EXEC]]></field>
                <field tag="52"><![CDATA[20240612-08:28:34.995]]></field>
                <field tag="56"><![CDATA[BANZAI]]></field>
            </header>
            <body>
                <field tag="6"><![CDATA[22]]></field>
                <field tag="11"><xsl:value-of select="message/body/field[@tag='11']"/></field>
                <field tag="14"><xsl:value-of select='java:InnerCommand.getValue("RandomInt(10000,50000)")'/></field>
                <field tag="17"><xsl:value-of select='java:InnerCommand.getValue("Sequence(./ExecID17.seq)")'/></field>
                <field tag="20"><![CDATA[0]]></field>
                <field tag="31"><xsl:value-of select='java:InnerCommand.getValue("RandomFloat(150,160)")'/></field>
                <field tag="32"><![CDATA[0]]></field>
                <field tag="37">OrderId<xsl:value-of select='java:InnerCommand.getValue("Sequence(./OrderID37.seq)")'/></field>
                <field tag="38"><xsl:value-of select="message/body/field[@tag='38']"/></field>
                <field tag="39"><![CDATA[0]]></field>
                <field tag="54"><xsl:value-of select="$side"/></field>
                <field tag="55"><xsl:value-of select="message/body/field[@tag='55']"/></field>
                <xsl:choose>
                    <xsl:when test="$side='1'">
                        <field tag="44"><![CDATA[151.22]]></field>
                    </xsl:when>
                    <xsl:when test="$side='2'">
                        <field tag="44"><![CDATA[150.11]]></field>
                    </xsl:when>
                    <xsl:otherwise>0.0</xsl:otherwise>
                </xsl:choose>
                <field tag="58"><![CDATA[MockPending]]></field>
                <field tag="60"><xsl:value-of select='java:InnerCommand.getValue("UTCDateTime()")'/></field>
                <field tag="75"><xsl:value-of select='java:InnerCommand.getValue("UTCDate()")'/></field>
            </body>
            <trailer>
                <field tag="10"><![CDATA[233]]></field>
            </trailer>
        </message>
    </xsl:template>
</xsl:stylesheet>
