package apps.training.components.topnav;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TopNav extends WCMUsePojo {

    private List<Page> items = new ArrayList<Page>();

    // Initializes the navigation
    public void activate() throws Exception {
        final Page rootPage = getCurrentPage().getAbsoluteParent(1);
        if (rootPage != null) {
            Iterator<Page> childPages = rootPage.listChildren(new
                    PageFilter(getRequest()), true);
            while (childPages.hasNext()) {
                items.add(childPages.next());
            }
        }
    }

    // Returns the navigation items
    public List<Page> getItems() {
        return items;
    }
}
