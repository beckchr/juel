<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="forrester://xsl/html/content2page.xsl"/>

  <!-- path to skin directory -->
	<xsl:param name="skin" select="'site://skin'"/>

  <!-- variant -->
  <xsl:param name="variant"/>

	<xsl:template match="/">
		<html>
			<xsl:apply-templates select="document($head)/head" />
      <body>
        <div id="header" >
          <xsl:call-template name="header"/>
          <xsl:apply-templates select="document($navigation)/*"/>
        </div>
        <div id="leftcol">
          <xsl:apply-templates select="document($menu)/*"/>
        </div>
        <div id="maincol" >
          <xsl:apply-templates select="*"/>
        </div>
        <div id="footer" >
          <xsl:call-template name="footer"/>
        </div>
      </body>
		</html>
	</xsl:template>

	<xsl:template name="header">
	    <a href="site:/">
	      <img src="{$skin}/images/juel-logo.gif" alt="JUEL"/>
	    </a>
	</xsl:template>

	<xsl:template name="footer">
		&#169; 2006-2014 Odysseus Software GmbH. All rights reserved.
	</xsl:template>

</xsl:stylesheet>
