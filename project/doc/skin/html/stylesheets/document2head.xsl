<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="forrester://xsl/html/document2head.xsl"/>

  <!-- path to skin directory -->
	<xsl:param name="skin" select="'site://skin'"/>

  <!-- variant -->
  <xsl:param name="variant"/>

  <!-- title -->
  <xsl:param name="title"/>

	<xsl:template name="other">
    <link rel="stylesheet" type="text/css" href="{$skin}/css/page.css"/>
    <link rel="stylesheet" type="text/css" href="{$skin}/css/content.css"/>
	</xsl:template>

</xsl:stylesheet>
