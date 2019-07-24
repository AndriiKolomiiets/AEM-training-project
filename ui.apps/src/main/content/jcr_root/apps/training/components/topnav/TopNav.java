package apps.training.components.topnav;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

public class TopNav extends WCMUsePojo {

    private List<Page> items = new ArrayList<Page>();

    // Initializes the navigation
    public void activate() throws Exception {
        final Page rootPage = getCurrentPage().getAbsoluteParent(1);
        if (rootPage != null) {
            Iterator<Page> childPages = rootPage.listChildren(new
                    PageFilter(getRequest()));
            while (childPages.hasNext()) {
                Page innerPage = childPages.next();
                items.add(innerPage);
                Iterator<Page> innerChildPages = innerPage.listChildren();
                while (innerChildPages.hasNext()) {
                    items.add(innerChildPages.next());
                }
            }
        }
    }

    // Returns the navigation items
    public List<Page> getItems() {
        return items;
    }
}