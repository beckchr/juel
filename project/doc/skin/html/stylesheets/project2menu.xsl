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
			  <a href="http://sourceforge.net/projects/juel/">
			  	<img src="http://sflogo.sourceforge.net/sflogo.php?group_id=165179&amp;type=2"
			  		width="125" height="37" border="0" alt="SF.net"/>
			  </a>
			  <p/>
			  <a href="http://stan4j.com">
				<img src="http://logo.stan4j.com/stan4j-125x40-t1.png" target="_blank"
					width="125" height="40" alt="stan4j.com" title="stan4j.com" border="0"/>
			  </a>			  
			  <p/>
			  <a href="http://musicmount.org">
				<img src="http://musicmount.org/img/musicmount-125x65.png" target="_blank"
					width="125" height="65" alt="MusicMount.org" title="MusicMount.org" border="0"/>
			  </a>			  
			</xsl:if>
		</div>
	</xsl:template>

</xsl:stylesheet>
