package boggle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import tree.LexicographicTree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class BoggleTest {
	private static final Set<String> EXPECTED_WORDS = new TreeSet<>(Arrays.asList(new String[] {"ces", "cesse", "cessent", "cresson", "ego", "encre",
			"encres", "engonce", "engoncer", "engonces", "esse", "gens", "gent", "gesse", "gnose", "gosse", "nes", "net", "nos", "once",
			"onces", "ose", "osent", "pre", "pres", "presse", "pressent", "ressent", "sec", "secs", "sen", "sent", "set", "son",
			"songe", "songent", "sons", "tenson", "tensons", "tes"}));
	private static final String GRID_LETTERS = "rhreypcswnsntego";
	String grid50x50 = "eymmccsrltjttsdiraoarliuniepeousrcgoiseerreeistiedtomcteevcmkaualilaretneerectresieenspgizeoeceecudsrrsrvfianrsicwtdieioeiufnidlaaeeoeieitmntleavieacalischvzeatuisiupatolauaernetasatttadvtthzraaneuzfpneenabiielhcnitesaouelsenxrtojlcastieklkrupeletaiztleapqgaeocbpteutnetrtozatluuarapepsvipesxolteatmylttumelctahsowlsadoelouamisparejpmuaasoaeszsuilubrdrannyosfewnolneudpatcrwatblttpensaaunvkslrekiittciivsomuestiurfuaxreeunuennetemubenanvsucimozentlvptnsoyaoatospesvaesasyysdlbdoraguhpleonvfrelentickiwzrnmimsaeimralovhetscejsdsnrtcsgporubtewesdklorlvteselauxieusieetfmiplllneuyprlpiiujiewverneussnnaxoaswclermderupyurmaareuescriqesbeeadldnlhtsnaucxeadstciqneeetcwtctcltavxgiiuorlomewbleeaoanrjeqeaqhzetmamisirasceranivleteeuaedeaatnsostwtbtonuasilsodhxsmnetecuoesepmotlndamvdcaeebiualneltdrtnwgerifterpepdetdbgollulneoynesonnrpesaustieundaevansmspaisinusitiaagrhoaeeewotnlagtlinjdssnocmeigvultkamnarvcloohslgiueawnyterddduepeislsmaemaiensuytiraesliehotcmaeoeovtsoiostialfertapbuptefeeleeonkeeectcdtneuidrlrpeenmeauvztltsetaeidlsrgscvlsenmetyeoueqesassooiajprrsytioqesugwvatixluutotimwlpesreeicylreeeseauueeeeapornntulivlonansipvoeeactiuecmeudnenrqaieordhluomrtsrmetetswlieqcmltslvsadeuspglmyruteoixiuoepdnectntentdaualdpcsoaeljvonkeftneiuedeeztsatencaectoeptluatriocdocrdtmudleueornptmeintlzejaaaneeradibraeaoaanpoisieeurtettrxvtneoegleltagkasosrastluadxsepnlsaadoaiepjswyedatmrsnivmriseaweinvatepciuuesssnllsssmixlesiedettssyoeuipwltetitececoieeozweaenmlaoroospptusidpkdvsrnqaajituspuuleiisheiogeinpbbsitbsvetofsncnaetaowooekmuntavroonjraduuacknoqqknnnrjoopeeofzdyseaoltsclvaaapiueceauofcdbntmxtneetpoitwiinfaeltgueeispzeacneqmviaiusaettplhiuaetqaewtfuuipoueuesnsoxaixaeeyavqllssqareessnmeolsetlvttbpbeoosesiuincpnersriiterrincnhsemaunvaseeueprldkiecnwtisultmmenensaojgidntetselyzagtctaisiraipzegeienjreosuuszuynlnpooesurddauuuitnouspiiuaeeqerelumdalnohdhueuuiiaiaaltlunnsnamleoprecysucviuirsatenctssjeniinreuenvirsntrwzntaeeieouapntlmayotrpsuunnuiptsxaevplkmuruasocuimontijceksmaeaaearurosaeitpimvtityevsualpsosallkkimiaaievplozjirncedcismssamnerotsprnltlhfiokolroleeaejexjslihseaoelqnsrwizluirhuraarefssdsealtkuediqtdpwekselinealineozeremtjandnrerracqoakiltrcsnwataavalommuslrdqawqpcneaiotajsaiedrkoxtasfyvermeyrnaibrdeiixlefsesvsqrlobkatcptiuxpmvanohcedlemkgevsuoexjjmenoteatptylewesoeotzbveiugseaswoeueoirpupdpulsidsiosueeealdepeltuwssipsecinicloeantylscemtsbairodutathtceeutmrsiarnptamasrrildiuwntaisaatculursrgeierrheeiteacuroruyfretvcxegadiiunguenunubreuflnccretdeetwmdunttrosyntooieeeutvenra";
	String grid10x10 = "eymmccsrltjttsdiraoarliuniepeousrcgoiseerreeistiedtomcteevcmkaualilaretneerectresieenspgizeoeceecuds";
	private static LexicographicTree dictionary = null;

	@BeforeAll
	private static void initTestDictionary() {
		System.out.print("Loading dictionary...");
		dictionary = new LexicographicTree("mots/dictionnaire_FR_sans_accents.txt");
		System.out.println(" done.");
	}
	
	@Test
	void wikipediaExample() {
		Boggle b = new Boggle(4, GRID_LETTERS, dictionary);
		assertNotNull(b);
		assertEquals(GRID_LETTERS, b.letters());
		assertTrue(b.contains("songent"));
		assertFalse(b.contains("sono"));
		assertEquals(EXPECTED_WORDS, b.solve());
	}
	
	@Test
	void fiftyExample() {
		Boggle b = new Boggle(50, grid50x50, dictionary);
		assertTrue(b.contains("dedicacas"));
	}
	
	
	// My tests
	// Constructors
	@Test
	void constructorNegativeSize() {		
		assertThrows(IllegalArgumentException.class, () -> new Boggle(-10, dictionary));
	}
	
	@Test
	void constructorNulSize() {		
		assertThrows(IllegalArgumentException.class, () -> new Boggle(0, dictionary));
	}

	@Test
	void constructorLettersLength() {
		// Given
		Boggle grid = new Boggle(5, dictionary);
		int counter = 0;
		
		// When
		counter = grid.letters().length();
		
		// Then
		assertEquals(25, counter);
	}
	
	
	
	
	@Test
	void constructor2NegativeSize() {		
		assertThrows(IllegalArgumentException.class, () -> new Boggle(-10, grid10x10, dictionary));
	}
	
	@Test
	void constructor2NulSize() {		
		assertThrows(IllegalArgumentException.class, () -> new Boggle(0, grid10x10, dictionary));
	}
	
	@Test
	void constructor2NotEnoughLetters() {		
		assertThrows(IllegalArgumentException.class, () -> new Boggle(20, grid10x10, dictionary));
	}
	
	@Test
	void constructor2TooMuchLetters() {		
		// Given
		Boggle grid = new Boggle(5, grid10x10, dictionary);
		int counter = 0;
		
		// When
		counter = grid.letters().length();
		
		// Then
		assertEquals(25, counter);
	}
	
	@Test
	void constructor2LettersLength() {
		// Given
		Boggle grid = new Boggle(10, grid10x10, dictionary);
		int counter = 0;
		
		// When
		counter = grid.letters().length();
		
		// Then
		assertEquals(100, counter);
	}
	

}
