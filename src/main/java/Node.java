import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class Node
{
    public static ConcurrentHashMap<String, Node> staticListAllNods = new ConcurrentHashMap<>();
    private static ConcurrentSkipListSet<String> staticListAllAttr = new ConcurrentSkipListSet<>();

    private Node parentNode;
    private String address;
    HashSet<Node> childNods;
    private int level;

    private Logger loggerStaticAttr = LogManager.getLogger("StaticAttribute");
    private Logger loggerNods = LogManager.getLogger("LoggerNods");

    public Node(Node parentNode, String address) {
        this.parentNode = parentNode;
        this.address = address;
        this.childNods = new HashSet<>();
        this.level = parentNode == null? 0 : (parentNode.level + 1);
        staticListAllNods.put(address, this);

    }

    protected void makeChildren() {
        Document doc = null;
        try {
            doc = Jsoup.connect(address).maxBodySize(0).get();
        } catch (IOException e) {
            return;
        }
        Elements elements = doc.select("a");
        if (elements.size() == 0){
            return;
        }

        for (Element element : elements) {
            if ((element.attr("href").startsWith("/") || element.attr("href")
                    .startsWith(address)) && !element.attr("href").contains("?")) {
                String attr = element.attr("href").replaceAll("https://skillbox.ru", "").trim();
                if (attr.length() > 1 && !staticListAllAttr.contains(attr)){
                    String[] splitAttr = attr.split("/");
                    String absoluteAddress = address.endsWith(splitAttr[1]) ? address.substring(0, (address.length() - (splitAttr[1].length()+1))) + attr : address + attr;
                   String linkChildren = absoluteAddress.endsWith("/") ? absoluteAddress.substring(0, absoluteAddress.length()-1) : absoluteAddress;
                    if (!isExistAddress(linkChildren)){
                        continue;
                    }
                    Node nodeChild = new Node(this, linkChildren);
                    childNods.add(nodeChild);
                    loggerStaticAttr.info(linkChildren);
                    staticListAllAttr.add (attr);
                }
            }
        }
    }

    private static boolean isExistAddress(String linkChildren){
        URL url;
        HttpURLConnection huc;
        int responseCode = 0;
        try {
            url = new URL(linkChildren);
            huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");
            responseCode = huc.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseCode != 404;
    }

    public static ConcurrentSkipListSet<String> getStaticListAllNodes() {
        return staticListAllAttr;
    }

    public String getAddress() {
        return address;
    }

    public HashSet<Node> getChildNods() {
        return childNods;
    }

    public int getLevel() {
        return level;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Node → " + address + " Level → " + level;
    }
}

