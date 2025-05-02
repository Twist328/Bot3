package bot;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CbrService {

    private static final String CBR_URL = "https://www.cbr.ru/scripts/XML_daily.asp";

    public String getRates() {
        try {
            URL url = new URL(CBR_URL);
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(url.openStream());

            doc.getDocumentElement().normalize();

            String usd = getCurrencyRate(doc, "USD");
            String eur = getCurrencyRate(doc, "EUR");
            String gbp = getCurrencyRate(doc, "GBP");
            String jpy = getCurrencyRate(doc, "JPY");

            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

            return String.format("""
                💱 Курсы валют ЦБ РФ на %s:
                🇺🇸 USD: %s ₽
                🇪🇺 EUR: %s ₽
                🇬🇧 GBP: %s ₽
                🇯🇵 100 JPY: %s ₽
                """, date, usd, eur, gbp, jpy);

        } catch (Exception e) {
            return "Ошибка получения курсов валют с сайта ЦБ РФ.";
        }
    }

    private String getCurrencyRate(Document doc, String charCode) {
        NodeList list = doc.getElementsByTagName("Valute");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            NodeList children = node.getChildNodes();

            String code = "";
            String value = "";

            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if ("CharCode".equals(child.getNodeName())) {
                    code = child.getTextContent();
                }
                if ("Value".equals(child.getNodeName())) {
                    value = child.getTextContent();
                }
            }

            if (code.equals(charCode)) return value;
        }
        return "—";
    }
}

