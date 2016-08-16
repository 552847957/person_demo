package com.wondersgroup.healthcloud.api.http.dto.identify;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class HealthQuestionAPIEnity {

	private String physical;
	private Map<String, String> advice;
	
	private static String[] DAILYLIFE;
	private static String[] DIET ;
	private static String[] EXERCISE ;
	private static String[] EMOTION ;
	private static String[] CARE ;
	static{
		DAILYLIFE = new String[]{
			"起居有常，保证充足的睡眠。平时注意保暖，不要汗出当风，避免过劳，以免损伤正气。多活动四肢，以流通气血",
			"起居有规律，保持充足的睡眠。可多晒太阳，带动体内阳气生发。注意保暖，尤其是背、腹和足部。不宜在阴暗潮湿的环境中生活。夏季避免长时间待在空调房中。秋冬应注意及时增添衣物，防止大汗或汗出当风",
			"起居应有规律，环境宜静，保证充足的睡眠，以养阴气。避免熬夜，中午宜午休。戒烟酒，节制房事，有利于养精。高温酷暑下工作，大汗、大渴等均可耗血伤阴，加重阴虚倾向。容易上火者，可多游泳",
			"居室最好朝阳。环境宜干燥，避免潮湿及受寒淋雨。常晒太阳，衣着应透气，宽松，有利于汗液蒸发，通达气机",
			"居处宜干燥、通风，不宜居住在低洼潮湿之地。起居要有规律，保持充足而有规律的睡眠。不要熬夜，避免过劳",
			"作息应有规律，保持充足的睡眠，不要熬夜。注意劳逸结合，动静结合，以免气机郁滞而导致血行不畅",
			"居住环境应宽敞明亮，有助于改善气郁的情绪。保持有规律的睡眠，睡前避免饮茶及咖啡等饮料。多参加有益的社会活动，减少在室内独处",
			"四季都应防止接触过敏原。季节交替之时，往往是过敏反应的多发时段，尤应注意。为增强对周围环境的适应能力，起居应有规律，保持充足的睡眠。居室应清洁，通风良好，被褥、床单经常洗晒，以防尘螨过敏。不宜养宠物，防止对动物皮毛过敏",
			"起居应有规律，合理作息，保持充足的睡眠。劳逸结合。顺应自然，遵循养生原则，通过综合调摄，使脏腑、气血、阴阳达到动态平衡"
		};
		DIET = new String[]{
			"注意饮食均衡，不宜多食生冷苦寒、辛辣燥热及过于滋腻、难消化以及有耗气作用食物。适宜食用具有益气健脾作用的食物，如粳米、小麦、小米、芡实、鸡肉、牛肉、鸡蛋、扁豆、山药、黄豆、土豆、红薯、栗子、大枣等",
			"可适当多食牛羊肉、鸡肉、鸡蛋、鳝鱼、韭菜、胡萝卜、生姜、辣椒、葱、蒜、花椒等甘温益气之品，少食黄瓜、柿子、螃蟹、苦瓜、荸荠、梨、西瓜等生冷寒凉的食物，少饮绿茶",
			"可适当多食瘦猪肉、鸭肉、龟鳖、海参、海蜇、冬瓜、黄瓜、荸荠、芝麻、百合、木耳、银耳、梨等滋润之品，少食羊肉、狗肉、韭菜、辣椒、葱、蒜等性温燥烈之品。但滋阴不宜太过，否则影响吸收",
			"饮食应以清淡为原则，适当多食冬瓜、苦瓜、山药、茼蒿、萝卜、扁豆、薏苡仁、紫菜、海藻等食物，有利于清肺化痰、健脾化湿。少食辛辣、肥甘厚味的食物",
			"饮食以清淡为原则。可多食鸭肉、鸭蛋、冬瓜、苦瓜、空心菜、芹菜、黄瓜、丝瓜、山药、藕、荸荠、绿豆、西瓜等甘寒、甘平的食物，忌温燥、辛辣和肥甘厚味，如羊肉、狗肉、鳝鱼、韭菜、生姜、芫荽、辣椒、花椒等。油炸、烧烤及烟酒等易加重湿热体质，应远离为好",
			"可多食油菜、洋葱、茄子、萝卜、胡萝卜、黑豆、黄豆、海带、紫菜、黑木耳、金桔、橙、柚、山楂、玫瑰花、醋等具有活血、散结、行气、通络等作用的食物，凡寒凉、酸涩、收敛、油腻之品均应忌食",
			"合理搭配膳食，饮食调养以理气、行气、疏肝的食物为主，可多食小麦、萝卜、刀豆、芫荽、黄花菜、海带、海藻、葱、蒜、柑橘、金桔、玫瑰花等，忌食寒凉、温燥、油腻、收涩的食物",
			"主动摸索适宜自己的膳食，避开可能导致过敏的食物，减少发作机会。一般来讲，饮食提倡清淡、均衡，荤素、粗细搭配合理。少食辛辣、腥膻发物及含致敏物质的食物，如辣椒、花椒、酒、海鲜、猪头肉、鲤鱼、竹笋等",
			"膳食平衡，食物多样化，做到合理搭配。戒烟限酒，过饥、过饱、过冷、过热、饮食不洁等均可影响健康，应予避免"
		};
		EXERCISE = new String[]{
			"运动应适度，循序渐进。每次时间不宜过长。可做一些缓和的运动，如散步、慢跑、太极拳、气功、做操等",
			"动能生阳，故要加强体育锻炼。适宜做一些舒缓柔和的运动，如慢跑、散步、打太极拳、做操等，运动强度控制在手脚温热、面色红润、微微出汗为度。冬天要避免在大风、大寒、大雾、大雪及空气污染等极端的环境中锻炼",
			"适宜中小强度的锻炼，可选择太极拳、气功、八段锦等动静结合、较为柔和的传统健身项目。锻炼时要控制出汗量，及时补充水分，不宜洗桑拿等",
			"平时多进行户外活动，根据自身情况开展锻炼，如散步、慢跑、爬山、球类、太极拳、游泳、武术等。运动强度要适宜，运动量逐渐增加",
			"适合做强度和运动量较大的锻炼，如中长跑、游泳、爬山、各种球类、武术等，有利于帮助湿热之邪排出体外。锻炼时，宜有氧运动和无氧运动相结合",
			"根据身体状况选择锻炼项目，如步行、慢跑、太极拳、徒手健身操等。中老年人可采取小负荷、多次数的锻炼方法。若运动时出现胸闷、呼吸困难等症状，应立即停止，并去医院检查",
			"坚持运动锻炼，户外大强度的运动锻炼有助于宣泄烦闷和转移注意力。常参加群众性的文娱体育活动，如长跑、登山、游泳、武术、打球、跳舞等",
			"积极参加体育锻炼，增强体质。如对花粉、柳絮等过敏，应避免在郊外、公园长时间运动和逗留；有过敏性鼻炎者，不宜在冬季进行户外锻炼",
			"根据年龄和性别的不同，参加适宜的运动，并持之以恒。调心、调息，使内外和谐，气血运行畅通"
		};
		EMOTION = new String[]{
			"以积极进取的态度面对生活，不可过于劳神，避免过度紧张，保持稳定平和的心态",
			"积极调畅情志，和喜怒、去忧悲、防惊恐，多与别人交流沟通。可多听一些舒缓和高亢的音乐以调动情绪",
			"保持稳定平和的心态，及时释放烦闷。培养兴趣爱好，如书法、绘画、下棋等，以怡情悦性。听音乐可选曲调舒缓、轻柔、抒情的曲子",
			"保持心境平和，避免思虑过度，节制大喜大悲。培养广泛的兴趣爱好，以舒畅情志，调畅气机",
			"培养广泛的兴趣爱好。若出现不良情绪，应自我调适，可采用节制、疏泄、转移等不同方法",
			"培养乐观开朗的性格，有利于气血顺畅，营卫调和。注意及时消除不良情绪，防止郁闷不乐而致气机不畅，平时可多听抒情柔缓的音乐来调节情志",
			"培养开朗豁达的性格，克服偏执，以平常心对待现实生活。主动寻找生活乐趣，广交朋友。平时可多听轻松或激昂的音乐",
			"因对外界适应能力差，应重视精神方面的自我调养。保持乐观向上的精神状态。不必过分在意自己的过敏体质，必要时可主动告诉别人自己的禁忌，赢得理解和帮助",
			"保持乐观开朗，积极进取。节制偏激的情感，及时消除生活中的不利事件对情绪的负面影响"
		};
		CARE = new String[]{
			"常按摩足三里、气海、关元、神阙等穴位",
			"常按摩足三里、涌泉、命门、肾俞等穴位",
			"可按摩三阴交、太溪等穴位",
			"常按摩丰隆、水道、足三里等穴位",
			"常按摩阴陵泉、阳陵泉等穴位",
			"常按摩血海、内关等穴位",
			"常按摩太冲、膻中、行间等穴位",
			"常按摩足三里、关元、神阙、肾俞等穴位",
			"常按摩足三里、气海等穴位"
		};
	}
	
	public HealthQuestionAPIEnity(){
		
	}
	public HealthQuestionAPIEnity(String physical){
		this.physical = physical;
		int index = this.getIndex();
		if(index != -1){
			this.advice = new HashMap<String, String>();
			this.advice.put("dailyLife", DAILYLIFE[index]);
			this.advice.put("diet", DIET[index]);
			this.advice.put("exercise", EXERCISE[index]);
			this.advice.put("emotion", EMOTION[index]);
			this.advice.put("care", CARE[index]);
		}
	}
	private int getIndex() {
		if(this.physical.equals("气虚质"))return 0;
		if(this.physical.equals("阳虚质"))return 1;
		if(this.physical.equals("阴虚质"))return 2;
		if(this.physical.equals("痰湿质"))return 3;
		if(this.physical.equals("湿热质"))return 4;
		if(this.physical.equals("血瘀质"))return 5;
		if(this.physical.equals("气郁质"))return 6;
		if(this.physical.equals("特禀质"))return 7;
		if(this.physical.contains("平和质"))return 8;
		return -1;
	}

	public String getPhysical() {
		return physical;
	}

	public void setPhysical(String physical) {
		this.physical = physical;
	}

	public Map<String, String> getAdvice() {
		return advice;
	}

	public void setAdvice(Map<String, String> advice) {
		this.advice = advice;
	}
}
