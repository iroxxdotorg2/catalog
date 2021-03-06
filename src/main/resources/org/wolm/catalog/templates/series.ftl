<h1>${series.title}</h1>
<table>
	<#-- cover art and description -->
	<#assign artLink = series.coverArtLink!'https://s3-us-west-2.amazonaws.com/wordoflife.mn.audio/etc/WordofLifeLogo-L.png' />
	<tr>
		<#if artLink??>
			<td valign="top"><img src="${artLink}" width="128"/></td>
		</#if>
		<td valign="top" <#if !series.coverArtLink??>colspan="2"</#if>>
			<b>${series.title}</b>
			
			<#if series.speakers?size &gt; 0 || series.StartDate??><br/></#if>

			<#-- speaker -->
			<#if series.speakers?size &gt; 0>
				<#list series.speakers as speaker>${speaker}<#if speaker_has_next>, </#if></#list>
			</#if>
			
			<#if (series.speakers?size &gt; 0) && (series.startDate??)>/</#if>

			<#-- dates -->
			<#if series.startDate??>
				<#if !(series.endDate??)>Started</#if> <#-- still in progress -->
				${series.startDate?date}
				<#if series.endDate?? && series.endDate?date != series.startDate?date>
					- ${series.endDate?date}
				</#if>
			</#if>
			(${series.messageCount} <#if series.messageCount == 1>message<#else>messages</#if>)
			
			<br/>
			${series.description!}
		</td>
	</tr>
	
</table>

<script type="text/javascript">
//<![CDATA[
	function togglePlayer(element) {
		jQuery('.player').not(element.children('.player')).hide('puff');
		element.children('.player').toggle('puff');
	}
	function mouseEnterMessage(element) {
		element.css('border-color','#ffd700');
	}
	function mouseExitMessage(element) {
		element.css('border-color','#3e713f');
	}
//]]>
</script>

<table width="100%">	
	<#-- messages -->
	<#list series.messages as message>
		<tr>
			<td onmouseover="mouseEnterMessage(jQuery(this));" onmouseout="mouseExitMessage(jQuery(this));" 
					style="border-style:solid;border-width:1px;border-color:#3e713f;">
				<div onclick="togglePlayer(jQuery(this).parent());" style="display:block;padding:1px;" title="${message.description!}">
					${message_index + 1}.
					<b>${message.title}</b>
					<#if message.speakers??>
						- <#list message.speakers as speaker>${speaker}<#if speaker_has_next>, </#if></#list>
					</#if>
					<#if message.date??>(${message.date?date})</#if>
				</div>
				<div class="player" style="display:none;">
					<p>${message.description!}</p>
					<table width="100%">
						<tr>
							<td width="60%" valign="top">
								<#if message.audioLink??>
									<audio controls style="width:100%;">
										<source src="${message.audioLink}" type="audio/mpeg" />
									</audio>
								<#else>
									no audio is available for this message
								</#if>
							</td>
							<#if message.videoLink??>
								<td width="20%" valign="top" align="right">
									<a href="${message.videoLink}" target="wolmVideo">
										<img src="https://s3-us-west-2.amazonaws.com/wordoflife.mn.audio/etc/YouTubeIcon.jpg" />
									</a>
								</td>
							</#if>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</#list>
	<#if series.studyGuideLinks??>
		<tr>
			<td style="border-style:solid;border-width:1px;border-color:gray">
				<b>Additional Materials</b>
					<#list series.studyGuideLinks as guide>
						<#assign name= guide?replace('.*/','','r') />
						<#assign name= name?replace('+',' ') />
						<br/><a href="${guide}" target="wolmGuide" style="padding-left:24px;">${name}</a>
					</#list>
			</td>
		</tr>
	</#if>
	
</table>