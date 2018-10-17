/* 
#   Copyright (c) 2017 European Commission  
#   Licensed under the EUPL, Version 1.2 or – as soon they will be 
#   approved by the European Commission - subsequent versions of the 
#    EUPL (the "Licence"); 
#    You may not use this work except in compliance with the Licence. 
#    You may obtain a copy of the Licence at: 
#    * https://joinup.ec.europa.eu/page/eupl-text-11-12  
#    *
#    Unless required by applicable law or agreed to in writing, software 
#    distributed under the Licence is distributed on an "AS IS" basis, 
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
#    See the Licence for the specific language governing permissions and limitations under the Licence.
 */

package member_country_specific.eavt;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSortedSet;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import eu.eidas.SimpleProtocol.Attribute;
import eu.eidas.SimpleProtocol.AuthenticationRequest;
import eu.eidas.SimpleProtocol.RequestedAuthenticationContext;
import eu.eidas.SimpleProtocol.utils.SimpleProtocolProcess;
import eu.eidas.auth.commons.EidasStringUtil;
import eu.eidas.auth.commons.attribute.AttributeDefinition;
import eu.eidas.auth.commons.attribute.AttributeRegistries;
import eu.eidas.auth.commons.attribute.AttributeRegistry;
import eu.eidas.auth.commons.protocol.eidas.LevelOfAssuranceComparison;
import eu.eidas.auth.commons.protocol.eidas.SpType;
import member_country_specific.sp.ApplicationContextProvider;
import member_country_specific.sp.Constants;
import member_country_specific.sp.Country;
import member_country_specific.sp.SPUtil;

public class EavtAction extends ActionSupport implements ServletRequestAware, ServletResponseAware {

	public static final String ACTION_COUNTRY_CHOICE = "country_choice";
	public static final String COUNTRY = "country";
	private static final long serialVersionUID = 3660074009157921579L;
	private static final Logger LOGGER = LoggerFactory.getLogger(EavtAction.class);
	private static final String ATTRIBUTES_FILENAME = "eidasAttributes.xml";
	private static final String ADDITIONAL_ATTRIBUTES_FILENAME = "additional-attributes.xml";
	private static final AttributeRegistry coreAttributeRegistry = AttributeRegistries.fromFile(ATTRIBUTES_FILENAME,
			null);
	private static final AttributeRegistry coreAdditionalAttributeRegistry = AttributeRegistries
			.fromFile(ADDITIONAL_ATTRIBUTES_FILENAME, SPUtil.getConfigFilePath());
	private static Properties configs;

	private HttpServletRequest request;

	private static List<Country> countries;
	private static String providerName;
	private static String returnUrl;
	private String id;
	private String citizen;
	private String smsspRequest;

	private static void loadGlobalConfig() {
		configs = SPUtil.loadSPConfigs();
		countries = new ArrayList<>();
		returnUrl = configs.getProperty(Constants.SP_RETURN);
	}

	private void populate() {

		EavtAction.loadGlobalConfig();

		int numCountries = Integer.parseInt(configs.getProperty(Constants.COUNTRY_NUMBER));
		for (int i = 1; i <= numCountries; i++) {
			final String url = getUrl(i);
			Country country = new Country(i, configs.getProperty(COUNTRY + Integer.toString(i) + ".name"), url,
					configs.getProperty(COUNTRY + Integer.toString(i) + ".countrySelector"));
			countries.add(country);
			LOGGER.info(country.toString());
		}

	}

	private String getUrl(int i) {
		final boolean isSpecificConnectorJar = (Boolean) ApplicationContextProvider.getApplicationContext()
				.getBean(Constants.SPECIFIC_CONNECTOR_JAR);
		final String url = configs.getProperty(COUNTRY + Integer.toString(i) + ".url");
		if (isSpecificConnectorJar) {
			return url;
		} else {
			return url.replace(Constants.EIDAS_NODE, Constants.SPECIFIC_CONNECTOR);
		}
	}

	public String processEavtRequest() {

		String id = request.getParameter("id");
		LOGGER.debug("param - id value: {}", id);
		providerName = "Българска агенция по нещо"; // TODO
		// returnUrl = "https://www.google.bg/";
		populate();
		return ACTION_COUNTRY_CHOICE;
	}

	public String processCountryChoice() throws JAXBException {

		String test = request.getParameter("test");
		LOGGER.debug("param - test value: ", test);

		return execute();
	}

	public String execute() throws JAXBException {

		ImmutableSortedSet<AttributeDefinition<?>> allSupportedAttributesSet = coreAttributeRegistry.getAttributes();
		ImmutableSortedSet<AttributeDefinition<?>> eidasAdditionalAttributeDefinitions = coreAdditionalAttributeRegistry
				.getAttributes();

		List<AttributeDefinition<?>> reqAttrList = new ArrayList<>(allSupportedAttributesSet);
		reqAttrList.addAll(eidasAdditionalAttributeDefinitions);

		// remove Representative Attributes
		for (AttributeDefinition<?> attributeDefinition : allSupportedAttributesSet) {
			String attributeName = attributeDefinition.getFriendlyName();
			if (attributeName.startsWith("Representative") || !attributeDefinition.isRequired()) {
				reqAttrList.remove(attributeDefinition);
			}
		}
		for (AttributeDefinition<?> attributeDefinition : eidasAdditionalAttributeDefinitions) {
			String attributeName = attributeDefinition.getFriendlyName();
			if (attributeName.startsWith("Representative") || !attributeDefinition.isRequired()) {
				reqAttrList.remove(attributeDefinition);
			}
		}

		// generate Json:
		final AuthenticationRequest authenticationRequest = new AuthenticationRequest();
		authenticationRequest.setId(id);
		authenticationRequest.setServiceUrl(returnUrl);
		authenticationRequest.setProviderName(providerName);
		authenticationRequest.setCitizenCountry(citizen);
		authenticationRequest.setNameIdPolicy("unspecified");

		authenticationRequest.setSpType(SpType.PUBLIC.toString());

		final RequestedAuthenticationContext requestedAuthenticationContext = new RequestedAuthenticationContext();
		final ArrayList<String> levelOfAssurances = new ArrayList<>();
		levelOfAssurances.add("A");
		requestedAuthenticationContext.setContextClass(levelOfAssurances);
		requestedAuthenticationContext.setComparison(LevelOfAssuranceComparison.MINIMUM.stringValue());
		authenticationRequest.setAuthContext(requestedAuthenticationContext);

		// attributes
		List<Attribute> simpleAttributes = new ArrayList<>();
		for (AttributeDefinition<?> attributeDefinition : reqAttrList) {
			final String friendlyName = attributeDefinition.getFriendlyName();
			final Attribute simpleAttribute = new Attribute();
			simpleAttribute.setName(friendlyName);
			simpleAttribute.setRequired(attributeDefinition.isRequired());
			simpleAttributes.add(simpleAttribute);
		}

		authenticationRequest.setAttributes(simpleAttributes);

		String smsspRequestJSON = new SimpleProtocolProcess().convert2Json(authenticationRequest);
		smsspRequest = EidasStringUtil.encodeToBase64(smsspRequestJSON);
		return Action.SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	@SuppressWarnings("squid:S1186")
	public void setServletResponse(HttpServletResponse response) {
	}

	public String getDefaultActionUrl() {
		// return getPostActionUrl();
		return "http://localhost:8080/SpecificConnector/ServiceProvider"; // TODO
	}

	public List<Country> getCountries() {
		return countries;
	}

	public String getProviderName() {
		return providerName;
	}

	public String getCitizen() {
		return citizen;
	}

	public void setCitizen(String citizen) {
		this.citizen = citizen;
	}

	public void setCitizenEidas(String citizen) {
		setCitizen(citizen);
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getSmsspRequest() {
		return smsspRequest;
	}

	public void setSmsspRequest(String smsspToken) {
		this.smsspRequest = smsspToken;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
