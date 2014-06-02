package kobic.msb.system.config;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import kobic.com.edgeR.EdgeR;
import kobic.msb.common.util.SwingUtilities;
import kobic.msb.system.PropertiesController;

public class ProjectConfiguration implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private float margin;
	private float offset;
	private Color recordBarColor;
	private Color fieldBarColor;

	private Color histogramBarColorStart;
	private Color histogramBarColorEnd;
	private Color _5p_upstream_color;
	private Color _3p_downstream_color;
	private Color _5p_mature_color;
	private Color _3p_mature_color;
	private Color _loop_color;
	
	private Color _missing_value_color_;
	private Color _total_sum_color_;
	private Color _group_sum_color_;
	private Color _expression_intensity_color_;
	private	String	normalizationMethod;
	private double	missingValue;
	
	private Color adenosineColor;
	private Color thymidineColor;
	private Color guanosineColor;
	private Color cytidineColor;
	private Color unkowonNucleotideColor;
	
	private Color misForegroundColor;
	private Color misBackgroundColor;
	private Color misABackgroundColor;
	private Color misTBackgroundColor;
	private Color misGBackgroundColor;
	private Color misCBackgroundColor;
	private Color misXBackgroundColor;

	private float blockWidth;
	private float blockHeight;
	private float alphaForVerticalHilightBar;
	private float alphaForHorizontalHilightBar;
	
	private Font	systemFont;
	private Font	systemBoldFont;
	
	private String configFilePath;
	
	
	private float expressionProfileLabelOffset;
	private float expressionProfileBlockHeight;
	private float expressionProfileBlockWidth;
	
	public static Map<String, String> map = ProjectConfiguration.initDefaultConfigurationMap();

	public static HashMap<String, String> initDefaultConfigurationMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("margin", 						"0");
		map.put("offset", 						"100");
		map.put("recordBarColor", 				"-65536");
		map.put("fieldBarColor", 				"-65536");
		map.put("blockWidth", 					"20");
		map.put("blockHeight", 					"15");
		map.put("alphaForVerticalHilightBar", 	"0.5");
		map.put("alphaForHorizontalHilightBar", "0.5");

		Color color = Color.white;
		map.put("adenosineColor",				Integer.toString(color.hashCode()));
		map.put("thymidineColor",				Integer.toString(color.hashCode()));
		map.put("guanosineColor",				Integer.toString(color.hashCode()));
		map.put("cytidineColor",				Integer.toString(color.hashCode()));
		map.put("unkowonNucleotideColor",		Integer.toString(color.hashCode()));

		map.put("misForegroundColor",			"-16777216");
		map.put("misBackgroundColor",			"-65536");

		map.put("misABackgroundColor",			Integer.toString( new Color(195, 255, 166).hashCode() ) );
		map.put("misTBackgroundColor",			Integer.toString( new Color(255, 170, 180).hashCode() ) );
		map.put("misGBackgroundColor",			Integer.toString( new Color(255, 240, 77).hashCode() ) );
		map.put("misCBackgroundColor",			Integer.toString( new Color(125, 191, 255).hashCode() ) );
		map.put("misXBackgroundColor",			Integer.toString( new Color(205, 83, 255).hashCode() ) );

		map.put("_5p_upstream_color",			"-13718273");
		map.put("_3p_downstream_color",			"-16776961");
		map.put("_5p_mature_color",				"-14336");
		map.put("_3p_mature_color",				"-16711936");
		map.put("_loop_color",					"-5085732");
		map.put("histogramBarColorStart",		"-6253825");
		map.put("histogramBarColorEnd",			"-990465");
		
		map.put("systemFont",					"Arial-PLAIN-11");
		map.put("systemBoldFont",				"Arial-BOLD-12");
		
		map.put("expressionProfileLabelOffset", "150");
		map.put("expressionProfileBlockHeight", "3");
		map.put("expressionProfileBlockWidth",	"3");

		map.put("_missing_value_color_",		"-1");
		map.put("_total_sum_color_",			"-1579033");
		map.put("_group_sum_color_",			"-1048581");
		map.put("_expression_intensity_color_", "-65536");
		map.put("normalizationMethod",			EdgeR._TMM_);
		map.put("missingValue",					"0.001");
		

		return (HashMap<String, String>) map;
	}

	private Map<String, String> getMapFromConfigObject() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("margin", 						Float.toString( this.getMargin() ) );
		map.put("offset", 						Float.toString( this.getOffset() ) );
		map.put("recordBarColor", 				SwingUtilities.getStringFromColor( this.getRecordBarColor() ) );
		map.put("fieldBarColor", 				SwingUtilities.getStringFromColor( this.getFieldBarColor() ) );
		map.put("blockWidth", 					Float.toString( this.getBlockWidth() ) );
		map.put("blockHeight", 					Float.toString( this.getBlockHeight() ) );
		map.put("alphaForVerticalHilightBar", 	Float.toString( this.getAlphaForVerticalHilightBar() ) );
		map.put("alphaForHorizontalHilightBar", Float.toString( this.getAlphaForHorizontalHilightBar() ) );
		
		map.put("_5p_upstream_color",			SwingUtilities.getStringFromColor( this.get_5p_upstream_color() ));
		map.put("_3p_downstream_color",			SwingUtilities.getStringFromColor( this.get_3p_downstream_color() ));
		map.put("_5p_mature_color",				SwingUtilities.getStringFromColor( this.get_5p_mature_color() ));
		map.put("_3p_mature_color",				SwingUtilities.getStringFromColor( this.get_3p_mature_color() ));
		map.put("_loop_color",					SwingUtilities.getStringFromColor( this.get_loop_color() ));
		map.put("histogramBarColorStart",		SwingUtilities.getStringFromColor( this.getHistogramBarColorStart() ) );
		map.put("histogramBarColorEnd",			SwingUtilities.getStringFromColor( this.getHistogramBarColorEnd() ) );
		
		map.put("adenosineColor",				SwingUtilities.getStringFromColor( this.getAdenosineColor() ) );
		map.put("thymidineColor",				SwingUtilities.getStringFromColor( this.getThymidineColor() ) );
		map.put("guanosineColor",				SwingUtilities.getStringFromColor( this.getGuanosineColor() ) );
		map.put("cytidineColor",				SwingUtilities.getStringFromColor( this.getCytidineColor() ) );
		map.put("unkowonNucleotideColor",		SwingUtilities.getStringFromColor( this.getUnkowonNucleotideColor() ) );
		map.put("misForegroundColor",			SwingUtilities.getStringFromColor( this.getMisForegroundColor() ) );
		map.put("misBackgroundColor",			SwingUtilities.getStringFromColor( this.getMisBackgroundColor() ) );
		map.put("misABackgroundColor",			SwingUtilities.getStringFromColor( this.getMisABackgroundColor() ) );
		map.put("misTBackgroundColor",			SwingUtilities.getStringFromColor( this.getMisTBackgroundColor() ) );
		map.put("misGBackgroundColor",			SwingUtilities.getStringFromColor( this.getMisGBackgroundColor() ) );
		map.put("misCBackgroundColor",			SwingUtilities.getStringFromColor( this.getMisCBackgroundColor() ) );
		map.put("misXBackgroundColor",			SwingUtilities.getStringFromColor( this.getMisXBackgroundColor() ) );

		map.put("systemFont",					this.getSystemFont().getName() + "-" + ProjectConfiguration.getFontStyle(this.getSystemFont()) + "-" + this.getSystemFont().getSize() );
		map.put("systemBoldFont",				this.getSystemBoldFont().getName() + "-" + ProjectConfiguration.getFontStyle(this.getSystemBoldFont()) + "-" + this.getSystemBoldFont().getSize() );
		
		map.put("expressionProfileLabelOffset", Float.toString( this.getExpressionProfileLabelOffset() ) );
		map.put("expressionProfileBlockHeight", Float.toString( this.getExpressionProfileBlockHeight() ) );
		map.put("expressionProfileBlockWidth",	Float.toString( this.getExpressionProfileBlockWidth() ) );
		
		map.put("_missing_value_color_",		SwingUtilities.getStringFromColor( this.get_missing_value_color_() ) );
		map.put("_total_sum_color_",			SwingUtilities.getStringFromColor( this.get_total_sum_color_() ) );
		map.put("_group_sum_color_",			SwingUtilities.getStringFromColor( this.get_group_sum_color_() ) );
		map.put("_expression_intensity_color_", SwingUtilities.getStringFromColor( this.get_expression_intensity_color_() ) );
		map.put("normalizationMethod",			this.getNormalizationMethod() );
		map.put("missingValue",					Double.toString( this.getMissingValue() ) );

		return map;
	}
	
	public static String getFontStyle(Font font) {
		if( font.isBold() && font.isItalic() )	return "BOLDITALIC";
		else if( font.isBold() )				return "BOLD";
		else if( font.isItalic() )				return "ITALIC";
		else									return "PLAIN";
	}

	public void writeConfigFile() {
		File projectConfig = new File( this.configFilePath );
		PropertiesController controller = new PropertiesController( projectConfig.getAbsolutePath(), "UTF-8" );
		controller.storeAll( this.getMapFromConfigObject() );
	}

	public ProjectConfiguration(String path) {
		this.configFilePath = path;
		this.reloadConfiguration();
	}

	public ProjectConfiguration( Properties property ) {
		this.refreshConfiguration( property );
	}

	public void reloadConfiguration() {
		File projectConfig = new File( this.configFilePath );

		PropertiesController controller = new PropertiesController( projectConfig.getAbsolutePath(), "UTF-8" );
		if( !projectConfig.exists() ) {
			controller.storeAll( ProjectConfiguration.map );
		}
		Properties property = controller.loadAll();
		this.refreshConfiguration( property );
	}

	private void refreshConfiguration( Properties property ) {
		this.margin							= Float.parseFloat( property.getProperty( "margin", ProjectConfiguration.map.get("margin") ) );
		this.offset							= Float.parseFloat( property.getProperty("offset", ProjectConfiguration.map.get("offset") ) );
	    
		this.recordBarColor 				= SwingUtilities.getColorFromString( property.getProperty("recordBarColor", ProjectConfiguration.map.get("recordBarColor")) );
		this.fieldBarColor 					= SwingUtilities.getColorFromString( property.getProperty("fieldBarColor", ProjectConfiguration.map.get("fieldBarColor")) );

		this.blockWidth						= Float.parseFloat( property.getProperty( "blockWidth", ProjectConfiguration.map.get("blockWidth") ) );
		this.blockHeight					= Float.parseFloat( property.getProperty("blockHeight", ProjectConfiguration.map.get("blockHeight") ) );
		this.alphaForVerticalHilightBar		= Float.parseFloat( property.getProperty( "alphaForVerticalHilightBar", ProjectConfiguration.map.get("alphaForVerticalHilightBar") ) );
		this.alphaForHorizontalHilightBar	= Float.parseFloat( property.getProperty("alphaForHorizontalHilightBar", ProjectConfiguration.map.get("alphaForHorizontalHilightBar") ) );
		
		this._5p_upstream_color				= SwingUtilities.getColorFromString( property.getProperty("_5p_upstream_color", ProjectConfiguration.map.get("_5p_upstream_color")) );
		this._3p_downstream_color			= SwingUtilities.getColorFromString( property.getProperty("_3p_downstream_color", ProjectConfiguration.map.get("_3p_downstream_color")) );
		this._5p_mature_color				= SwingUtilities.getColorFromString( property.getProperty("_5p_mature_color", ProjectConfiguration.map.get("_5p_mature_color")) );
		this._3p_mature_color				= SwingUtilities.getColorFromString( property.getProperty("_3p_mature_color", ProjectConfiguration.map.get("_3p_mature_color")) );
		this._loop_color					= SwingUtilities.getColorFromString( property.getProperty("_loop_color", ProjectConfiguration.map.get("_loop_color")) );
		this.histogramBarColorStart			= SwingUtilities.getColorFromString( property.getProperty("histogramBarColorStart", ProjectConfiguration.map.get("histogramBarColorStart")) );
		this.histogramBarColorEnd			= SwingUtilities.getColorFromString( property.getProperty("histogramBarColorEnd", ProjectConfiguration.map.get("histogramBarColorEnd")) );

		this.adenosineColor					= SwingUtilities.getColorFromString( property.getProperty("adenosineColor", ProjectConfiguration.map.get("adenosineColor")) );
		this.thymidineColor					= SwingUtilities.getColorFromString( property.getProperty("thymidineColor", ProjectConfiguration.map.get("thymidineColor")) );
		this.guanosineColor					= SwingUtilities.getColorFromString( property.getProperty("guanosineColor", ProjectConfiguration.map.get("guanosineColor")) );
		this.cytidineColor					= SwingUtilities.getColorFromString( property.getProperty("cytidineColor", ProjectConfiguration.map.get("cytidineColor")) );
		this.unkowonNucleotideColor			= SwingUtilities.getColorFromString( property.getProperty("unkowonNucleotideColor", ProjectConfiguration.map.get("unkowonNucleotideColor")) );
		this.misForegroundColor				= SwingUtilities.getColorFromString( property.getProperty("misForegroundColor", ProjectConfiguration.map.get("misForegroundColor")) );
		this.misBackgroundColor				= SwingUtilities.getColorFromString( property.getProperty("misBackgroundColor", ProjectConfiguration.map.get("misBackgroundColor")) );
		
		this.misABackgroundColor				= SwingUtilities.getColorFromString( property.getProperty("misABackgroundColor", ProjectConfiguration.map.get("misABackgroundColor")) );
		this.misTBackgroundColor				= SwingUtilities.getColorFromString( property.getProperty("misTBackgroundColor", ProjectConfiguration.map.get("misTBackgroundColor")) );
		this.misGBackgroundColor				= SwingUtilities.getColorFromString( property.getProperty("misGBackgroundColor", ProjectConfiguration.map.get("misGBackgroundColor")) );
		this.misCBackgroundColor				= SwingUtilities.getColorFromString( property.getProperty("misCBackgroundColor", ProjectConfiguration.map.get("misCBackgroundColor")) );
		this.misXBackgroundColor				= SwingUtilities.getColorFromString( property.getProperty("misXBackgroundColor", ProjectConfiguration.map.get("misXBackgroundColor")) );
		
		this.systemFont						= Font.decode( property.getProperty( "systemFont", 		ProjectConfiguration.map.get("systemFont")) );
		this.systemBoldFont					= Font.decode( property.getProperty( "systemBoldFont",	ProjectConfiguration.map.get("systemBoldFont")) );
		
		this.expressionProfileLabelOffset	= Float.parseFloat( property.getProperty("expressionProfileLabelOffset", ProjectConfiguration.map.get("expressionProfileLabelOffset")) );
		this.expressionProfileBlockHeight	= Float.parseFloat( property.getProperty("expressionProfileBlockHeight", ProjectConfiguration.map.get("expressionProfileBlockHeight")) );
		this.expressionProfileBlockWidth	= Float.parseFloat( property.getProperty("expressionProfileBlockWidth", ProjectConfiguration.map.get("expressionProfileBlockWidth")) );
		
		this._missing_value_color_			= SwingUtilities.getColorFromString( property.getProperty("_missing_value_color_", ProjectConfiguration.map.get("_missing_value_color_")) );
		this._total_sum_color_				= SwingUtilities.getColorFromString( property.getProperty("_total_sum_color_", ProjectConfiguration.map.get("_total_sum_color_")) );
		this._group_sum_color_				= SwingUtilities.getColorFromString( property.getProperty("_group_sum_color_", ProjectConfiguration.map.get("_group_sum_color_")) );
		this._expression_intensity_color_	= SwingUtilities.getColorFromString( property.getProperty("_expression_intensity_color_", ProjectConfiguration.map.get("_expression_intensity_color_")) );
		this.normalizationMethod			= property.getProperty( "normalizationMethod", ProjectConfiguration.map.get("normalizationMethod") );
		this.missingValue					= Double.parseDouble( property.getProperty( "missingValue", ProjectConfiguration.map.get("missingValue") ) );
	}
	
	public Font getSystemFont(){
		return this.systemFont;
	}
	
	public void setSystemFont(Font font) {
		this.systemFont = font;
	}
	
	public Font getSystemBoldFont(){
		return this.systemBoldFont;
	}
	
	public void setSystemBoldFont(Font font) {
		this.systemBoldFont = font;
	}
	
	public String getConfigFilePath()	{
		return this.configFilePath;
	}

	public Color getHistogramBarColorStart() {
		return histogramBarColorStart;
	}

	public void setHistogramBarColorStart(Color histogramBarColorStart) {
		this.histogramBarColorStart = histogramBarColorStart;
	}
	
	public Color getHistogramBarColorEnd() {
		return histogramBarColorEnd;
	}

	public void setHistogramBarColorEnd(Color histogramBarColorEnd) {
		this.histogramBarColorEnd = histogramBarColorEnd;
	}

	public float getMargin() {
		return margin;
	}
	public void setMargin(float margin) {
		this.margin = margin;
	}
	public float getOffset() {
		return offset;
	}
	public void setOffset(float offset) {
		this.offset = offset;
	}
	public Color getRecordBarColor() {
		return recordBarColor;
	}
	public void setRecordBarColor(Color recordBarColor) {
		this.recordBarColor = recordBarColor;
	}
	public Color getFieldBarColor() {
		return fieldBarColor;
	}
	public void setFieldBarColor(Color filedBarColor) {
		this.fieldBarColor = filedBarColor;
	}
	public float getBlockWidth() {
		return blockWidth;
	}
	public void setBlockWidth(float blockWidth) {
		this.blockWidth = blockWidth;
	}
	public float getBlockHeight() {
		return blockHeight;
	}
	public void setBlockHeight(float blockHeight) {
		this.blockHeight = blockHeight;
	}
	public float getAlphaForVerticalHilightBar() {
		return alphaForVerticalHilightBar;
	}
	public void setAlphaForVerticalHilightBar(float alphaForVerticalHilightBar) {
		this.alphaForVerticalHilightBar = alphaForVerticalHilightBar;
	}
	public float getAlphaForHorizontalHilightBar() {
		return alphaForHorizontalHilightBar;
	}
	public void setAlphaForHorizontalHilightBar(float alphaForHorizontalHilightBar) {
		this.alphaForHorizontalHilightBar = alphaForHorizontalHilightBar;
	}
	public Color get_5p_upstream_color() {
		return _5p_upstream_color;
	}

	public void set_5p_upstream_color(Color _5p_upstream_color) {
		this._5p_upstream_color = _5p_upstream_color;
	}

	public Color get_3p_downstream_color() {
		return _3p_downstream_color;
	}

	public void set_3p_downstream_color(Color _3p_downstream_color) {
		this._3p_downstream_color = _3p_downstream_color;
	}

	public Color get_5p_mature_color() {
		return _5p_mature_color;
	}

	public void set_5p_mature_color(Color _5p_mature_color) {
		this._5p_mature_color = _5p_mature_color;
	}

	public Color get_3p_mature_color() {
		return _3p_mature_color;
	}

	public void set_3p_mature_color(Color _3p_mature_color) {
		this._3p_mature_color = _3p_mature_color;
	}

	public Color get_loop_color() {
		return _loop_color;
	}

	public void set_loop_color(Color _loop_color) {
		this._loop_color = _loop_color;
	}

	public Color getAdenosineColor() {
		return adenosineColor;
	}

	public void setAdenosineColor(Color adenosineColor) {
		this.adenosineColor = adenosineColor;
	}

	public Color getThymidineColor() {
		return thymidineColor;
	}

	public void setThymidineColor(Color thymidineColor) {
		this.thymidineColor = thymidineColor;
	}

	public Color getGuanosineColor() {
		return guanosineColor;
	}

	public void setGuanosineColor(Color guanosineColor) {
		this.guanosineColor = guanosineColor;
	}

	public Color getCytidineColor() {
		return cytidineColor;
	}

	public void setCytidineColor(Color cytidineColor) {
		this.cytidineColor = cytidineColor;
	}

	public Color getUnkowonNucleotideColor() {
		return unkowonNucleotideColor;
	}

	public void setUnkowonNucleotideColor(Color unkowonNucleotideColor) {
		this.unkowonNucleotideColor = unkowonNucleotideColor;
	}

	public Color getMisForegroundColor() {
		return misForegroundColor;
	}

	public void setMisForegroundColor(Color misForegroundColor) {
		this.misForegroundColor = misForegroundColor;
	}

	public Color getMisBackgroundColor() {
		return misBackgroundColor;
	}

	public void setMisBackgroundColor(Color misBackgroundColor) {
		this.misBackgroundColor = misBackgroundColor;
	}

	public float getExpressionProfileLabelOffset() {
		return expressionProfileLabelOffset;
	}

	public void setExpressionProfileLabelOffset(float expressionProfileLabelOffset) {
		this.expressionProfileLabelOffset = expressionProfileLabelOffset;
	}
	
	
	public float getExpressionProfileBlockHeight() {
		return expressionProfileBlockHeight;
	}

	public void setExpressionProfileBlockHeight(float expressionProfileBlockHeight) {
		this.expressionProfileBlockHeight = expressionProfileBlockHeight;
	}

	public float getExpressionProfileBlockWidth() {
		return expressionProfileBlockWidth;
	}

	public void setExpressionProfileBlockWidth(float expressionProfileBlockWidth) {
		this.expressionProfileBlockWidth = expressionProfileBlockWidth;
	}

	public Color get_missing_value_color_() {
		return _missing_value_color_;
	}

	public void set_missing_value_color_(Color _missing_value_color_) {
		this._missing_value_color_ = _missing_value_color_;
	}

	public Color get_total_sum_color_() {
		return _total_sum_color_;
	}

	public void set_total_sum_color_(Color _total_sum_color_) {
		this._total_sum_color_ = _total_sum_color_;
	}

	public Color get_group_sum_color_() {
		return _group_sum_color_;
	}

	public void set_group_sum_color_(Color _group_sum_color_) {
		this._group_sum_color_ = _group_sum_color_;
	}

	public Color get_expression_intensity_color_() {
		return _expression_intensity_color_;
	}

	public void set_expression_intensity_color_(Color _expression_intensity_color_) {
		this._expression_intensity_color_ = _expression_intensity_color_;
	}

	public String getNormalizationMethod() {
		return normalizationMethod;
	}

	public void setNormalizationMethod(String normalizationMethod) {
		this.normalizationMethod = normalizationMethod;
	}

	public double getMissingValue() {
		return missingValue;
	}

	public void setMissingValue(double missingValue) {
		this.missingValue = missingValue;
	}

	public Color getMisABackgroundColor() {
		return misABackgroundColor;
	}

	public void setMisABackgroundColor(Color misABackgroundColor) {
		this.misABackgroundColor = misABackgroundColor;
	}

	public Color getMisTBackgroundColor() {
		return misTBackgroundColor;
	}

	public void setMisTBackgroundColor(Color misTBackgroundColor) {
		this.misTBackgroundColor = misTBackgroundColor;
	}

	public Color getMisGBackgroundColor() {
		return misGBackgroundColor;
	}

	public void setMisGBackgroundColor(Color misGBackgroundColor) {
		this.misGBackgroundColor = misGBackgroundColor;
	}

	public Color getMisCBackgroundColor() {
		return misCBackgroundColor;
	}

	public void setMisCBackgroundColor(Color misCBackgroundColor) {
		this.misCBackgroundColor = misCBackgroundColor;
	}

	public Color getMisXBackgroundColor() {
		return misXBackgroundColor;
	}

	public void setMisXBackgroundColor(Color misXBackgroundColor) {
		this.misXBackgroundColor = misXBackgroundColor;
	}
}
