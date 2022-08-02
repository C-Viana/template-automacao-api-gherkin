package steps.barrigarest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import io.cucumber.java.AfterAll;
import io.cucumber.java.AfterStep;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import reporter.ReportManager;

public class BarrigaRestSteps extends BarrigaRestUtils {
	
	private static Map<String, String> userData;
	private static String TOKEN = "";
	
	private RequestSpecification request;
	private QueryableRequestSpecification queryableReq;
	private Response response;
	
	private BarrigaRestConta contaCadastrada;
	private BarrigaRestMovimentacao movimentacao;
	
	@BeforeAll
	public static void setup() {
		RestAssured.baseURI = BARRIGAREST_BASE_URL;
        RestAssured.port = BARRIGAREST_PORT;
        RestAssured.basePath = BARRIGAREST_BASE_PATH;
        
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.setContentType(BARRIGAREST_CONTENT_TYPE);
        RestAssured.requestSpecification = reqBuilder.build();
        
        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectResponseTime(Matchers.lessThan(DEFAULT_WAIT_TIME.toNanos()));
        RestAssured.responseSpecification = resBuilder.build();
        
        userData = new HashMap<String, String>();
        userData.put("email", "cv.test@test.com.br");
        userData.put("senha", "123456");
	}
	
	@AfterStep
	public void afterTestStep(Scenario scenario) {
		if(scenario.isFailed())
			ReportManager.setTestStep(response);
	}
	
	@AfterAll
    public static void endTests() {
        getTransactions().stream().forEach( e -> deleteTransaction(e.getId()) );
        getAccounts().stream().forEach( e -> deleteAccount(e.getId()) );
    }
	
	
	@Given("que tento acessar a aplicação sem um token de acesso")
	public void queAcessarAAplicacaoSemUmTokenDeAcesso() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.get("/contas");
        ReportManager.setTestStep(queryableReq);
	}
	
	@Then("vejo que não é possível acessar a aplicação")
	public void vejoQueNaoEPossivelAcessarAAplicacao() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		response.then().statusCode(401);
		ReportManager.setTestStep(response);
	}
	
	@Given("que realizo meu logon na aplicação")
	public void queRealizoMeuLogonNaAplicacao() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().body(userData).when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.post("/signin");
        
        response.then().statusCode(200);
        if(TOKEN.isBlank() || TOKEN.isEmpty()) {
        	TOKEN = "JWT " + response.then().extract().path("token");
            RestAssured.requestSpecification.header("Authorization", TOKEN);
        }
        
        ReportManager.setTestStep(response);
	}
	
	@And("realizo a inclusão de uma nova conta")
	public void realizoAInclusaoDeUmaNovaConta() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().body("{\"nome\" : \"Conta de Teste "+ getTimeSuffix() +"\"}").when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.post("/contas");
        
        ReportManager.setTestStep(queryableReq);
	}
	
	@Then("confirmo que a conta foi gerada com sucesso")
	public void confirmoQueAContaFoiGeradaComSucesso() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		response.then().statusCode(201);
		ReportManager.setTestStep(response);
	}
	
	@And("tento incluir uma conta com um nome já registrado")
	public void tentoIncluirUmaContaComUmNomeJaRegistrado() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		contaCadastrada = createAccount();
		request = given().body("{\"nome\" : \""+ contaCadastrada.getNome() + "\"}").when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.post("/contas");
        
        ReportManager.setTestStep(queryableReq);
	}
	
	@Then("confirmo não ser possível incluir contas com nomes iguais")
	public void confirmoNaoSerPossivelIncluirContasComNomesIguais() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		response.then().statusCode(400).body("error", is("Já existe uma conta com esse nome!"));
		ReportManager.setTestStep(response);
	}
	
	@And("seleciono uma conta cadastrada")
	public void selecionoUmaContaCadastrada() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		contaCadastrada = createAccount();
		ReportManager.setTestStep();
	}
	
	@When("alterar o nome desta conta")
	public void alterarONomeDestaConta() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().body("{\"nome\" : \"Conta Alterada por API " + getTimeSuffix() + "\"}").when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.put("/contas/" + contaCadastrada.getId());
        
        ReportManager.setTestStep(queryableReq);
	}
	
	@Then("confirmo que nome foi alterado com sucesso")
	public void confirmoQueNomeFoiAlteradoComSucesso() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		response.then().statusCode(200);
		
		ReportManager.setTestStep(response);
	}
	
	@When("tentar incluir uma movimentação nesta conta sem informar os dados obrigatórios")
	public void tentarIncluirUmaMovimentacaoNestaContaSemInformarOsDadosObrigatorios() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.post("/transacoes");
        
        ReportManager.setTestStep();
	}
	
	@Then("confirmo que não é possível incluir a movimentação sem esses dados")
	public void confirmoQueNaoEPossivelIncluirAMovimentacaoSemEssesDados() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		response.then().statusCode(400);
		JsonPath json = response.then().extract().jsonPath();
		MatcherAssert.assertThat(json.getList("$"), hasSize(8));
		MatcherAssert.assertThat(json.getList("msg", String.class), hasItems(
				"Data da Movimentação é obrigatório",
				"Data do pagamento é obrigatório",
				"Descrição é obrigatório",
				"Interessado é obrigatório",
				"Valor é obrigatório",
				"Valor deve ser um número",
				"Conta é obrigatório",
				"Situação é obrigatório"));
		
		ReportManager.setTestStep(response);
	}
	
	@When("tentar incluir uma movimentação com uma data futura para pagamento")
	public void tentarIncluirUmaMovimentacaoComUmaDataFuturaParaPagamento() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		movimentacao = new BarrigaRestMovimentacao(
				"Pagamento programado para acabamento de cômodo",
				"Azulejista",
				"DESP",
				getDateAsString(5, null, null),
				"04/07/2022",
				3500.00f,
				true,
				contaCadastrada.getId());
		
		request = given().body(movimentacao).when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.post("/transacoes");
        
        ReportManager.setTestStep(queryableReq);
	}
	
	@Then("confirmo que não é possível incluir a movimentação com esta data")
	public void confirmoQueNaoEPossivelIncluirAMovimentacaoComEstaData() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		response.then()
			.statusCode(400)
			.body("msg", hasItem("Data da Movimentação deve ser menor ou igual à data atual"));
		
		ReportManager.setTestStep(response);
	}
	
	@And("seleciono uma conta cadastrada com movimentações")
	public void selecionoUmaContaCadastradaComMovimentacoes() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		contaCadastrada = createAccount();
		movimentacao = createTransaction(contaCadastrada, getDateAsString(-20, null, null), getDateAsString(-8, null, null));
		
		ReportManager.setTestStep();
	}
	
	@When("consultar a lista de movimentações desta conta")
	public void consultarAListaDeMovimentacoesDestaConta() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.get("/saldo");
        
        ReportManager.setTestStep();
	}
	
	@Then("confirmo que todos os dados de saldos serão informados")
	public void confirmoQueTodosOsDadosDeSaldosSeraoInformados() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		JsonPath json = response.then().statusCode(200).extract().jsonPath();
		
		List<Double> lista = json.getList("saldo", Double.class);
		double total = 0.0;
		for (Double double1 : lista) {
			total += double1;
		}
		MatcherAssert.assertThat(total, is(not(0.0)) );
		
		ReportManager.setTestStep(response);
	}
	
	@When("tentar excluir essa conta")
	public void tentarExcluirEssaConta() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.delete("/contas/" + contaCadastrada.getId());
        
        ReportManager.setTestStep();
	}
	
	@Then("confirmo que não é possível excluir uma conta com movimentações")
	public void confirmoQueNaoEPossivelExcluirUmaContaComMovimentacoes() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
	    response.then()
			.statusCode(500)
			.body("name", is("error"))
			.body("detail", endsWith("is still referenced from table \"transacoes\"."));
		
		ReportManager.setTestStep(response);
	}
	
	@When("tentar excluir uma movimentação")
	public void tentarExcluirUmaMovimentacao() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
		request = given().when();
		queryableReq = SpecificationQuerier.query(request);
        response = request.delete("/transacoes/" + movimentacao.getId());
        
        ReportManager.setTestStep();
	}
	
	@Then("confirmo que a movimentação é excluída com sucesso")
	public void confirmoQueAMovimentacaoEExcluidaComSucesso() {
		ReportManager.setStepName(new Object(){}.getClass().getEnclosingMethod().getAnnotations()[0].toString());
	    response.then().statusCode(204);
		
		ReportManager.setTestStep(response);
	}
	
}
