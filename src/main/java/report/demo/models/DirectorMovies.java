
package report.demo.models;

public class DirectorMovies {
  private String director;
  private long revenue;
  private long budget;
  private long profitLoss;

  public void setDirector(String director) { this.director = director; }
  public String getDirector() { return this.director; }

  public void setRevenue(Long revenue) { this.revenue = revenue; }
  public Long getRevenue() { return this.revenue; }

  public void setBudget(Long budget) { this.budget = budget; }
  public Long getBudget() { return this.budget; }

  public void setProfitLoss(Long profitLoss) { this.profitLoss = profitLoss; }
  public Long getProfitLoss() { return this.profitLoss; }

}
