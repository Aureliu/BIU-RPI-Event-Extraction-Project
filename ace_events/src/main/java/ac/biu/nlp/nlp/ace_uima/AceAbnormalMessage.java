package ac.biu.nlp.nlp.ace_uima;

import org.apache.log4j.Logger;

public class AceAbnormalMessage extends Throwable {

	private static final long serialVersionUID = 8620565715724278040L;

    public AceAbnormalMessage(String shortMessage, String longMessage, Logger logger) {
    	super(shortMessage);
    	logger.error(longMessage);
    }
    
    public AceAbnormalMessage(String shortMessage) {
    	super(shortMessage);
    }
}
