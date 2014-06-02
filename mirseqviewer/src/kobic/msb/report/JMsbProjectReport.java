package kobic.msb.report;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

import java.math.BigDecimal;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.datasource.DRDataSource;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

public class JMsbProjectReport {
	public JMsbProjectReport() {
//		build();
	}

	public JRViewer build() {
		JasperPrint jp = null;

		FontBuilder boldFont = stl.fontArialBold().setFontSize(12);

		TextColumnBuilder<String> itemColumn = col.column("Item", "item", type.stringType());
		TextColumnBuilder<Integer> quantityColumn = col.column("Quantity", "quantity", type.integerType());
		TextColumnBuilder<BigDecimal> unitPriceColumn = col.column("Unit price", "unitprice", type.bigDecimalType());

		try {
			jp = report()
				.setTemplate(Templates.reportTemplate)
				.columns(itemColumn, quantityColumn, unitPriceColumn)
				.title(Templates.createTitleComponent("AreaChart"))
				.summary(
					cht.areaChart()
						.setTitle("Area chart")
						.setTitleFont(boldFont)
						.setCategory(itemColumn)
						.series(
							cht.serie(quantityColumn), cht.serie(unitPriceColumn))
						.setCategoryAxisFormat(
							cht.axisFormat().setLabel("Item")))
				.pageFooter(Templates.footerComponent)
				.setDataSource(createDataSource())
				.toJasperPrint();
		} catch (DRException e) {
			e.printStackTrace();
		}
		
		return new JRViewer(jp);
	}

	private JRDataSource createDataSource() {
		DRDataSource dataSource = new DRDataSource("item", "quantity", "unitprice");
		dataSource.add("Tablet", 350, new BigDecimal(300));
		dataSource.add("Laptop", 300, new BigDecimal(500));
		dataSource.add("Smartphone", 450, new BigDecimal(250));
		return dataSource;
	}
}
