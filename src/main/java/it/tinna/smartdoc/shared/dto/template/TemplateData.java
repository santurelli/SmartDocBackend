package it.tinna.smartdoc.shared.dto.template;

import java.util.Map;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class TemplateData {

	private JRBeanCollectionDataSource dataSource;
	private Map<String, Object> parameters;

	public JRBeanCollectionDataSource getDataSource() {
		return dataSource;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setDataSource(JRBeanCollectionDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

}

