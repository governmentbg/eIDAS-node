<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html lang="en">

<head>
<jsp:include page="htmlHead.jsp" />
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="tab-content">
				<div role="tabpanel" class="tab-pane fade in active" id="tab-02">
					<div class="col-md-12">
						<h2>
							<s:property value="%{providerName}" />
						</h2>
					</div>
					<jsp:include page="leftColumn.jsp" />
					<div class="col-md-6">
						<form id="redirectForm" name="redirectForm"
							action="<s:property value="defaultActionUrl" />" method="POST">


							<input type="hidden" id="SMSSPRequest" name="SMSSPRequest"
								value="<s:property value="smsspRequest"/>"> <input
								type="hidden" name="sendmethods" id="postmethod" value="POST" />
						</form>

						<noscript>
							<form id="noJavaScriptForm" name="noJavaScriptForm"
								action="<s:property value="defaultActionUrl" />" method="POST">

								<input type="hidden" id="SMSSPRequest" name="SMSSPRequest"
									value="<s:property value="smsspRequest"/>"> <input
									type="hidden" name="sendmethods" id="postmethod" value="POST" />

								<div class="button-group">
									<button id="submit_saml" type="submit"
										class="btn btn-default btn-lg btn-block">Submit</button>
								</div>
							</form>
						</noscript>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript"
		src="resources/skin0/js/redirectOnload.js"></script>
</body>
</html>
