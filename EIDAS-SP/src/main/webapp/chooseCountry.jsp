<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html lang="en">


<head>
<jsp:include page="htmlHead.jsp" />
<link href="css/dd.css" rel="stylesheet" type="text/css" />
<title><s:property value="%{getText('tituloId')}" /></title>
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
			<!--START NAV TAB-->

			<!--END NAV TAB-->
			<!--START TAB-->
			<div class="tab-content">

				<!--START TAB-02-->
				<!-- ******************************************************************************************************************************** -->
				<!-- ************************************************* TABBED PANEL 2 EIDAS attributes*********************************************** -->
				<!-- ******************************************************************************************************************************** -->
				<div role="tabpanel" class="tab-pane fade in active" id="tab-02">
					<div class="col-md-12">
						<h2>
							<s:property value="%{providerName}" />
							<span class="sub-title">(submits to an <span
								class="lowercase">e</span>IDAS Authentication Service)
							</span>
						</h2>
					</div>
					<jsp:include page="leftColumn.jsp" />
					<div class="col-md-6">
						<form action="CountryChoice" id="formTab2" method="POST">
							<div class="form-group" id="citizenCountryDivEidas">
								<label for="citizenEidas"><s:property
										value="%{getText('citizenCountryId')}" /></label> <select
									name="citizenEidas" id="citizeneidas" class="form-control">
									<option>Choose an option</option>
									<s:iterator value="countries">
										<option value="<s:property value="name" />"
											data-image="img/flags/<s:property value="name"/>.gif"><s:property
												value="name" /></option>
									</s:iterator>
								</select>
							</div>

							<input type="hidden" id="returnUrl" name="returnUrl"
								value="<s:property value="returnUrl"/>"> <input
								type="hidden" id="id" name="id" value="<s:property value="id"/>">

							<input type="submit" id="submit_tab2"
								class="btn btn-default btn-lg btn-block" value="Submit" />
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%
		/*end container*/
	%>
	<jsp:include page="footer.jsp" />
</body>