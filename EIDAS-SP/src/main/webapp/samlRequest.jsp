<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
	String eidasconnector = request.getParameter("eidasconnector");
%>
<html lang="en">

<head>
<jsp:include page="htmlHead.jsp" />
<title><s:property value="%{getText('tituloId')}" /></title>
<script type="text/javascript" src="js/sp.js"></script>
</head>
<body>

	<!--START HEADER-->
	<header class="header">
		<div class="container">
			<h1>
				<s:property value="%{getText('tituloCabeceraId')}" />
			</h1>
		</div>
	</header>
	<!--END HEADER-->
	<div class="container">
		<div class="row">
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane fade in active" id="tab-02">
					<div class="col-md-12">
						<h2>
							<s:property value="%{providerName}" />
							<span class="sub-title">SmsspRequest generated by the SP</span>
						</h2>
					</div>
					<jsp:include page="leftColumn.jsp" />
					<div class="col-md-6">
						<form id="countrySelector" name="countrySelector"
							action="<s:property value="defaultActionUrl" />" target="_parent"
							onsubmit="return setSAMRequestMethod();">
							<div class="form-group">
								<input type="hidden" id="SMSSPRequest" name="SMSSPRequest"
									value="<s:property value="smsspRequest"/>">
							</div>
							<div class="form-group">
								<div class="radio-inline-group">
									<div class="radio radio-info radio-inline">
										<input type="radio" name="sendmethods" id="postmethod"
											style="display: none" value="POST" checked
											onChange="ajaxChangeHttpMethod('samlRequestXML','changeProtocolBinding.action', receiveSignedRequest,errorAjaxRequest)" />
									</div>
								</div>
							</div>
							<div class="button-group">
								<button style='display: block;' id="submit_saml" type="button"
									class="btn btn-default btn-lg btn-block"
									onclick="$('#countrySelector').submit();">Submit</button>
							</div>
						</form>
						<form id="samlRequestXML" name="samlRequestXML" action="reSign">
							<div class="form-group">
								<label for="country"><s:property
										value="%{getText('destinationCountryId')}" /></label> <input
									type="text" name="country"
									value="<s:property value="citizen"/>" id="country"
									class="form-control" />
							</div>
							<div class="form-group">

								<label for="smsspRequestJSON"><s:property
										value="%{getText('SMSSPRequestJSONId')}" /></label>
								<textarea class="form-control" rows="20" id="smsspRequestJSON"
									name="smsspRequestJSON"><s:property
										value="smsspRequestJSON" /></textarea>
							</div>
						</form>
						<noscript>
							<form id="noJavaScriptForm" name="noJavaScriptRedirectForm"
								action="<%=eidasconnector%>" method="post">
								<div class="form-group">
									<input type="hidden" name="eidasconnector" id="eidasconnector"
										class="form-control" value="<%=eidasconnector%>" /> <input
										type="hidden" name="eidasconnector2"
										value="<s:property value="defaultActionUrl"/>"
										id="eidasconnector2" class="form-control" /> <input
										type="hidden" id="SMSSPRequestNoJS" name="SMSSPRequest"
										value=${smsspRequest }/>
								</div>
								<input type="submit" id="submitButton1" class="btn btn-next"
									value="Submit" />
							</form>
						</noscript>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" />
	<script type="text/javascript"
		src="resources/skin0/js/redirectOnload.js"></script>
</body>
</html>