package steps.barrigarest;

import static io.restassured.RestAssured.given;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import common.StaticResources;
import io.restassured.path.json.JsonPath;

public class BarrigaRestUtils implements StaticResources {
    
    public static String getTimeSuffix() {
        return DateTimeFormatter.ofPattern("ddMMyyy-HHmmss").format(LocalDateTime.now(ZoneId.of("GMT-3")));
    }

    public static String getDateAsString(Integer daysDif, Integer monthsDif, Integer yearsDif) {
        LocalDate date = LocalDate.now();

        if (daysDif != null)
            date = date.plusDays(daysDif);
        if (monthsDif != null)
            date = date.plusMonths(monthsDif);
        if (yearsDif != null)
            date = date.plusYears(yearsDif);
        
        return DateTimeFormatter.ofPattern("dd/MM/YYYY").format(date);
    }
    
    public static List<BarrigaRestConta> getAccounts() {
        JsonPath json = given()
            .when()
            .get("/contas")
            .then()
            .statusCode(200)
            .extract().jsonPath();
        return json.getList("$", BarrigaRestConta.class);
    }
    
    public static BarrigaRestConta getAccountByName(String nomeDaConta) {
        JsonPath json = given()
            .when()
            .get("/contas")
            .then()
            .statusCode(200)
            .extract().jsonPath();
        return json.getObject("find {it.nome=\"" + nomeDaConta + "\"}", BarrigaRestConta.class);
    }
    
    public static List<BarrigaRestMovimentacao> getTransactions() {
        JsonPath json = given()
            .when()
            .get("/transacoes")
            .then()
            .statusCode(200)
            .extract().jsonPath();
        return json.getList("$", BarrigaRestMovimentacao.class);
    }
    
    public static BarrigaRestMovimentacao getTransaction(String descricaoDaMovimentacao) {
        JsonPath json = given()
            .when()
            .get("/transacoes")
            .then()
            .statusCode(200)
            .extract().jsonPath();
        return json.getObject("find {it.descricao=\"" + descricaoDaMovimentacao + "\"}", BarrigaRestMovimentacao.class);
    }
    
    public static BarrigaRestConta createAccount() {
        JsonPath json = null;
        List<BarrigaRestConta> contas = getAccounts();

        if (contas.size() == 0) {
            json = given()
                .body("{\"nome\" : \"Conta de Teste " + getTimeSuffix() + "\"}")
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
                .extract().jsonPath();
            return json.getObject("$", BarrigaRestConta.class);
        }
        return contas.get(0);
    }
    
    public static void deleteAccount(Long accountId) {
        given()
        .when()
        .delete("/contas/" + accountId)
        .then()
        .statusCode(204);
    }
    
    public static BarrigaRestMovimentacao createTransaction(BarrigaRestConta conta, String dataEmissao, String dataPagamento) {
        JsonPath json = null;
        List<BarrigaRestMovimentacao> movs = getTransactions();
        if (movs.size() == 0 || !movs.stream().anyMatch(e -> e.getConta_id() == conta.getId())) {
            BarrigaRestMovimentacao movimentacao = new BarrigaRestMovimentacao(
                    "Pagamento de Teste",
                    "Seu Barriga",
                    "DESP",
                    dataEmissao,
                    dataPagamento,
                    300.00f,
                    true,
                    conta.getId());
            
            json = given()
                .body(movimentacao)
                .when()
                .post("/transacoes")
                .then()
                .statusCode(201)
                .extract().jsonPath();
            return json.getObject("$", BarrigaRestMovimentacao.class);
        }
        return movs.stream().filter(e -> e.getConta_id() == conta.getId()).findFirst().get();
    }
    
    public static void deleteTransaction(long transactionId) {
        given()
        .when()
        .delete("/transacoes/" + transactionId)
        .then()
        .statusCode(204);
    }
    
}
