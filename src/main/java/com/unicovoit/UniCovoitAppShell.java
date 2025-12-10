package com.unicovoit;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;

@Theme("unicovoit")   // 👈 this matches your folder: frontend/themes/unicovoit
public class UniCovoitAppShell implements AppShellConfigurator {
    // no code needed; the interface is a marker
}
