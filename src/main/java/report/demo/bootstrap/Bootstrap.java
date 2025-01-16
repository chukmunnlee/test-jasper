package report.demo.bootstrap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import report.demo.models.DirectorReport;
import report.demo.models.DirectorMovies;

@Component
public class Bootstrap implements CommandLineRunner {

  public static final String REPORT = "director_movies_report.jasper";


  @Override
  public void run(String... args) throws Exception {

    // Step 1 - Get JasperReport class object
    System.out.printf("STEP 1 - Loading report\n");
    JasperReport jasperReport = (JasperReport)JRLoader.loadObject(new File(REPORT));

    // Step 2 - Create JRDatasource
    System.out.printf("STEP 2 - Creating datasource\n");
    JRBeanCollectionDataSource  reportDataset 
        = new JRBeanCollectionDataSource(Bootstrap.createReport("Fred Flintstone", "B"));

    //JRBeanCollectionDataSource  directorMovieDataset 
        //= new JRBeanCollectionDataSource(Bootstrap.createDirectorMovies());

    // Step 3 - Create a hashmap to store parameters
    System.out.printf("STEP 3 - Binding datasource to report\n");
    Map<String, Object> parameters = new HashMap<>();
    // parameters.put("DIRECTOR_MOVIES_DATA_SOURCE", directorMovieDataset);
    parameters.put("REPORT_DATASOURCE", reportDataset);
    //parameters.put("DIRECTOR_MOVIES_DATASET", directorMovieDataset);

    // Step 4 - Create JasperPrint object
    System.out.printf("STEP 4 - Generating PDF\n");
    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, reportDataset);
    var pdfFile = JasperExportManager.exportReportToPdf(jasperPrint);

    // Step 5 - Export JasperPrint object to PDF
    String fileName = "director_movies_%d.pdf".formatted((new Date()).getTime());
    System.out.printf("STEP 5 - Writing file %s\n".formatted(fileName));

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
