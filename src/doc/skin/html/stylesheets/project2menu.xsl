<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="forrester://xsl/html/project2menu.xsl"/>

  <!-- path to skin directory -->
	<xsl:param name="skin" select="'site://skin'"/>

  <!-- variant -->
  <xsl:param name="variant"/>

  <!-- selected document id -->
	<xsl:param name="selection"/>

	<xsl:template match="project">
		<div class="menu">
			<xsl:call-template name="items"/>
			<xsl:if test="$variant = 'site' and $selection = 'juel/index'">
			  <p/>
			  <a href="http://sourceforge.net">
			  	<img src="http://sflogo.sourceforge.net/sflogo.php?group_id=165179&amp;type=1"
			  		width="88" height="31" border="0" alt="SF.net"/>
			  </a>
			  <br/>
			  <a href="http://stan4j.com">
				<img src="http://logo.stan4j.com/stan4j-88x31-t0.png" target="_blank"
					width="88" height="31" alt="stan4j.com" title="stan4j.com" border="0"/>
			  </a>			  
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>
