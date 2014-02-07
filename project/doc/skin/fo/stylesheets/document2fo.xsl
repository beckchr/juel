<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">
  <xsl:import href="forrester://xsl/fo/document2fo.xsl"></xsl:import>

  <xsl:param name="variant"/>
  <xsl:param name="doublesided" select="'false'"/>
  <xsl:param name="outermargin" select="'.64in'"/>

  <xsl:param name="font-size" select="'9.5pt'"/>
  <xsl:param name="font-family" select="'Bitstream Vera Sans'"/>
  <xsl:param name="font-size-table" select="'90%'"/>
  <xsl:param name="font-family-table" select="'Vera Sans'"/>
  <xsl:param name="font-size-monospace" select="'95%'"/>
  <xsl:param name="font-family-monospace" select="'Bitstream Vera Sans Mono'"/>
  <xsl:param name="font-size-monospace-source" select="'90%'"/>

  <xsl:param name="link-color" select="'#000000'"/>
  <xsl:param name="link-decoration" select="'none'"/>

  <xsl:variable name="show-external-urls" select="'false'"/>

  <xsl:param name="footertext">
    <xsl:text>Copyright &#169; 2006-2014 Odysseus Software GmbH. All rights reserved.</xsl:text>
  </xsl:param>
  <xsl:param name="footerurl">http://juel.sourceforge.net/</xsl:param>

  <xsl:template match="header/title">
    <fo:block text-align="center" font-size="18pt" font-weight="bold">
      <xsl:apply-templates/>
    </fo:block>

    <fo:block text-align="center" padding-before=".75in" padding-after=".75in">
      <fo:external-graphic src="skin/images/juel-logo.gif"/>
    </fo:block>
  </xsl:template>

	<xsl:template name="section-header-size">
    <xsl:param name="level"/>
    <xsl:value-of select="14-number($level)"/><xsl:text>pt</xsl:text>
	</xsl:template>

</xsl:stylesheet>
