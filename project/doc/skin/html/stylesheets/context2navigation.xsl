<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="forrester://xsl/html/context2navigation.xsl"/>

  <!-- path to skin directory -->
	<xsl:param name="skin" select="'site://skin'"/>

  <!-- variant -->
  <xsl:param name="variant"/>

  <xsl:template name="separator">
		<xsl:value-of select="' | '"/>
  </xsl:template>

</xsl:stylesheet>
