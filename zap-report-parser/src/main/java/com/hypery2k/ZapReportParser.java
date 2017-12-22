package com.hypery2k;

import br.com.softplan.security.zap.api.report.ZapReport;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author hypery2k
 */
public class ZapReportParser {

    public static final String RISKCODE = "riskcode";

    /**
     * Read zap report and search for highest risk code
     *
     * @param report
     * @return highest risk code
     */
    public int getHighestRiskCode(ZapReport report) {
        try {
            return this.getHighestRiskCode(report.getXmlReportAsString());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Read xml report and search for highest risk code
     *
     * @param xmlReport to use
     * @return highest risk code
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public int getHighestRiskCode(String xmlReport) throws ParserConfigurationException, SAXException, IOException {
        int highestFoundRiskCode = 0;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setCoalescing(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(xmlReport)));
        // loop through all found risk codes
        final NodeList riskCodes = document.getElementsByTagName(RISKCODE);
        for (int i = 0; i < riskCodes.getLength(); i++) {
            int riskCode = Integer.parseInt(riskCodes.item(i).getTextContent());
            if (riskCode > highestFoundRiskCode) {
                highestFoundRiskCode = riskCode;
            }

        }
        return highestFoundRiskCode;

    }
}
