<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="xml" indent="yes"/>

<xsl:strip-space elements="*"/>
<xsl:preserve-space elements="xsl:text"/>

<!-- copy elements by default -->
<xsl:template match="*">
    <xsl:copy>
        <xsl:copy-of select="@*"/>
        <xsl:apply-templates/>
    </xsl:copy>
</xsl:template>

<!-- where there are include elements, apply templates to (i.e. copy)
     the content of the xsl:stylesheet element in the document that
     they reference -->
<xsl:template match="xsl:include">
    <xsl:apply-templates select="document(@href)/*/*"/>
</xsl:template>

</xsl:stylesheet>