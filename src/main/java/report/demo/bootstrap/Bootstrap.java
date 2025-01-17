package report.demo.bootstrap;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JsonDataCollection;
import net.sf.jasperreports.json.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import report.demo.models.DirectorReport;
import report.demo.models.DirectorMovies;

@Component
public class Bootstrap implements CommandLineRunner {

  public static final String DIRECTOR_REPORT = "vttp_batch5_paf/director_movies_report.jasper";
  public static final String CART_REPORT = "vttp_batch5_paf/shopping_cart.jasper";

  @Override
  public void run(String... args) throws Exception {
    generateOrder(args);
  }

  public void generateOrder(String... args) throws Exception {
    System.out.printf("STEP 1 - Loading report\n");
    JasperReport jasperReport = (JasperReport)JRLoader.loadObject(new File(CART_REPORT));
    
    // Step 2 - Create JsonDataSource
    System.out.printf("STEP 2 - Creating datasource\n");
    JsonObject order = Json.createObjectBuilder()
        .add("order_id", UUID.randomUUID().toString().substring(0, 8))
        .add("name", "Fred Flintstone")
        .add("address", "1 Bedrock Ave")
        .build();

    JsonArray orderDetail = Json.createArrayBuilder()
        .add(
            Json.createObjectBuilder()
              .add("itemName", "Apple")
              .add("quantity", 10)
              .add("unitPrice", 0.5)
              .build()
          )
          .add(
            Json.createObjectBuilder()
              .add("itemName", "Orange")
              .add("quantity", 5)
              .add("unitPrice", 0.5)
              .build()
          )
          .add(
            Json.createObjectBuilder()
              .add("itemName", "Pear")
              .add("quantity", 5)
              .add("unitPrice", 0.75)
              .build()
          )
        .build();

    JsonDataSource orderDS = new JsonDataSource(
        new ByteArrayInputStream(order.toString().getBytes()));

    JsonDataSource orderDetailDS = new JsonDataSource(
        new ByteArrayInputStream(orderDetail.toString().getBytes()));
    //List<JsonDataSource> list = new LinkedList<>();
    //list.add(orderDetailDS);
    //JsonDataCollection<JsonDataSource> orderDetailCol = new JsonDataCollection<>(list);

    System.out.printf("STEP 3 - Binding datasource to report\n");
    Map<String, Object> params = new HashMap<>();
    params.put("ORDER_DETAIL_JSON_DATASET", orderDetailDS);
    //
    // Step 4 - Create JasperPrint object
    System.out.printf("STEP 4 - Generating PDF\n");
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, orderDS);
    var pdfFile = JasperExportManager.exportReportToPdf(jasperPrint);
    //
    // Step 5 - Export JasperPrint object to PDF
    String fileName = "order_%d.pdf".formatted((new Date()).getTime());
    System.out.printf("STEP 5 - Writing file %s\n".formatted(fileName));

    writePDF(fileName, pdfFile);
  }


  public void generateDirectorMovie(String... args) throws Exception {

    // Step 1 - Get JasperReport class object
    System.out.printf("STEP 1 - Loading report\n");
    JasperReport jasperReport = (JasperReport)JRLoader.loadObject(new File(DIRECTOR_REPORT));

    // Step 2 - Create JRDatasource
    System.out.printf("STEP 2 - Creating datasource\n");
    DirectorReport dirRep = new DirectorReport();
    dirRep.setName("Fred Flintstone");
    dirRep.setBatch("B");
    JRBeanCollectionDataSource  reportDataset 
        = new JRBeanCollectionDataSource(Bootstrap.createReport("Fred Flintstone", "B"));

    JRBeanCollectionDataSource  directorMovieDataset 
        = new JRBeanCollectionDataSource(Bootstrap.createDirectorMovies());

    // Step 3 - Create a hashmap to store parameters
    System.out.printf("STEP 3 - Binding datasource to report\n");
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("REPORT_DATASOURCE", reportDataset);
    parameters.put("DIRECTOR_TABLE_DATASET", directorMovieDataset);

    // Step 4 - Create JasperPrint object
    System.out.printf("STEP 4 - Generating PDF\n");
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, reportDataset);
    var pdfFile = JasperExportManager.exportReportToPdf(jasperPrint);

    // Step 5 - Export JasperPrint object to PDF
    String fileName = "director_movies_%d.pdf".formatted((new Date()).getTime());
    System.out.printf("STEP 5 - Writing file %s\n".formatted(fileName));

    writePDF(fileName, pdfFile);

  }

  public static void writePDF(String fileName, byte[] pdfFile) throws Exception {
    try (FileOutputStream fos = new FileOutputStream(fileName)) {
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      bos.write(pdfFile);
      bos.flush();
      fos.flush();
      bos.close();
    }
  }

  public static List<DirectorReport> createReport(String name, String batch) {
    List<DirectorReport> reportList = new LinkedList<>();
    DirectorReport report = new DirectorReport();
    report.setName(name);
    report.setBatch(batch);
    reportList.add(report);
    return reportList;
  }

  public static List<DirectorMovies> createDirectorMovies() {
    List<DirectorMovies> directorList = new LinkedList<>();

    DirectorMovies m = new DirectorMovies();
    m.setDirector("ABC");
    m.setRevenue(100L);
    m.setBudget(10L);
    m.setProfitLoss(m.getRevenue() - m.getBudget());
    directorList.add(m);

    m = new DirectorMovies();
    m.setDirector("DEF");
    m.setRevenue(50L);
    m.setBudget(100L);
    m.setProfitLoss(m.getRevenue() - m.getBudget());
    directorList.add(m);

    m = new DirectorMovies();
    m.setDirector("XYZ");
    m.setRevenue(500L);
    m.setBudget(70L);
    m.setProfitLoss(m.getRevenue() - m.getBudget());
    directorList.add(m);

    return directorList;
  }

}
