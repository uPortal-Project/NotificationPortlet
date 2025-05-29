package org.jasig.portlet.notice;

import net.sf.ehcache.Cache;
import org.jasig.portlet.notice.action.favorite.FavoriteNotificationServiceDecorator;
import org.jasig.portlet.notice.action.hide.HideNotificationServiceDecorator;
import org.jasig.portlet.notice.action.read.ReadNotificationServiceDecorator;
import org.jasig.portlet.notice.controller.NotificationLifecycleController;
import org.jasig.portlet.notice.service.CacheNotificationService;
import org.jasig.portlet.notice.service.classloader.ClassLoaderResourceNotificationService;
import org.jasig.portlet.notice.service.filter.FilteringNotificationServiceDecorator;
import org.jasig.portlet.notice.service.rome.RomeNotificationService;
import org.jasig.portlet.notice.util.NotificationResponseFlattener;
import org.jasig.portlet.notice.util.UsernameFinder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
public class NotificationTestConfig {
    @Bean
    public UsernameFinder usernameFinder() {
        UsernameFinder finder = new UsernameFinder();
        return finder;
    }

    @Bean
    public NotificationResponseFlattener notificationResponseFlattener() {
        return new NotificationResponseFlattener();
    }

    @Bean
    public NotificationLifecycleController notificationLifecycleController() {
        return new NotificationLifecycleController();
    }

    @Bean("rootNotificationService")
    public INotificationService rootNotificationService() {
        HideNotificationServiceDecorator hideDecorator = new HideNotificationServiceDecorator();
        hideDecorator.setEnclosedNotificationService(readNotificationService());
        return hideDecorator;
    }

    @Bean
    public INotificationService readNotificationService() {
        ReadNotificationServiceDecorator readDecorator = new ReadNotificationServiceDecorator();
        readDecorator.setEnclosedNotificationService(favoriteNotificationService());
        return readDecorator;
    }

    @Bean
    public INotificationService favoriteNotificationService() {
        FavoriteNotificationServiceDecorator favoriteDecorator = new FavoriteNotificationServiceDecorator();
        favoriteDecorator.setEnclosedNotificationService(cacheNotificationService());
        return favoriteDecorator;
    }

    @Bean
    public CacheNotificationService cacheNotificationService() {
        CacheNotificationService service = new CacheNotificationService();
        service.setName("notificationCacheNotificationService");

        List<INotificationService> embeddedServices = new ArrayList<>();

        FilteringNotificationServiceDecorator classLoaderDecorator = new FilteringNotificationServiceDecorator();
        classLoaderDecorator.setEnclosedNotificationService(classLoaderResourceNotificationService());
        embeddedServices.add(classLoaderDecorator);

        FilteringNotificationServiceDecorator romeDecorator = new FilteringNotificationServiceDecorator();
        romeDecorator.setEnclosedNotificationService(romeNotificationService());
        embeddedServices.add(romeDecorator);

        service.setEmbeddedServices(embeddedServices);
        return service;
    }

    @Bean
    public ClassLoaderResourceNotificationService classLoaderResourceNotificationService() {
        ClassLoaderResourceNotificationService service = new ClassLoaderResourceNotificationService();
        service.setName("classLoaderResourceNotificationService");
        return service;
    }

    @Bean
    public RomeNotificationService romeNotificationService() {
        RomeNotificationService service = new RomeNotificationService();
        service.setName("romeNotificationService");
        return service;
    }

    @Bean("ClassLoaderResourceNotificationService.responseCache")
    public Cache classLoaderResourceNotificationServiceCache() {
        return new Cache("ClassLoaderResourceNotificationService.responseCache",
                10000, false, false, 300, 300);
    }

    @Bean("RomeNotificationService.feedCache")
    public Cache romeFeedCache() {
        return new Cache("RomeNotificationService.feedCache",
                10000, false, false, 300, 300);
    }

    @Bean("notificationResponseCache")
    public Cache notificationResponseCache() {
        return new Cache("notificationResponseCache",
                10000, false, false, 300, 300);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCommonMessages(new Properties() {{
            put("notice.date.format", "MM/dd/yyyy HH:mm:ss");
        }});
        return messageSource;
    }
}
