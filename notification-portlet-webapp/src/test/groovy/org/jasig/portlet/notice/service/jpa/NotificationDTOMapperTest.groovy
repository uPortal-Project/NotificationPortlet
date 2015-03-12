package org.jasig.portlet.notice.service.jpa;

import org.dozer.DozerBeanMapper
import org.dozer.config.BeanContainer
import org.dozer.util.HibernateProxyResolver
import org.hibernate.proxy.HibernateProxy
import org.hibernate.proxy.LazyInitializer
import org.jasig.portlet.notice.NotificationState
import org.jasig.portlet.notice.rest.ActionDTO;
import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.AttributeDTO;
import org.jasig.portlet.notice.rest.EntryDTO
import org.jasig.portlet.notice.rest.EventDTO
import org.jasig.portlet.notice.rest.RecipientDTO;
import org.jasig.portlet.notice.rest.RecipientType;
import org.junit.Before;
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

import java.sql.Timestamp

/**
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@RunWith(BlockJUnit4ClassRunner)
public class NotificationDTOMapperTest extends GroovyTestCase {
    private DozerBeanMapper mapper;
    private defaultCompareFn;
    private Timestamp now = new Timestamp(System.currentTimeMillis());


    @Before
    public void setUp() {
        // technically, this is more of an integration test, but the test
        // isn't as useful if I mock all this stuff, so bend the rules a bit
        BeanContainer.getInstance().setProxyResolver(new HibernateProxyResolver());

        def listener = new NotificationDTOMapperEventListener();
        listener.setPostProcessorMap([
            (JpaEntry.class): new JpaEntryPostProcessor()
        ]);

        mapper = new DozerBeanMapper(
            mappingFiles: ['mapping/jpa-mappings.xml'],
            eventListeners: [listener],
            customFieldMapper: new LazyInitializationMapper()
        );

        defaultCompareFn = { obj1, obj2 -> return obj1.name == obj2.name; }
    }


    @Test
    public void testMapJpaEntry() {
        def dto = new EntryDTO(
            id: 1,
            title: 'title',
            image: 'image',
            body: 'body',
            attributes: [
                new AttributeDTO(name: 'name1', values: ['val1', 'val2']),
                new AttributeDTO(name: 'name2', values: ['val2', 'val3', 'val4'])
            ],
            addressees: [
                new AddresseeDTO(name: "user1", type: RecipientType.INDIVIDUAL, recipients: [
                    new RecipientDTO(id: 100, username: 'test-user1'),
                    new RecipientDTO(id: 101, username: 'test-user2')
                ]),
                new AddresseeDTO(name: "group1", type: RecipientType.GROUP, recipients: [
                    new RecipientDTO(id: 200, username: 'test-group1')
                ])
            ],
            actions: [
                new ActionDTO(id: 1, label: 'action1', clazz: 'org.jasig.test.Class1'),
                new ActionDTO(id: 2, label: 'action2', clazz: 'org.jasig.test.Class2')
            ]
        );

        JpaEntry jpa = mapper.map(dto, JpaEntry.class);
        assertMatches(jpa, dto);
    }


    @Test
    public void testMapJpaEntryWithLazyCollections() {
        def dto = new EntryDTO(
            id: 1,
            title: 'title',
            image: 'image',
            body: 'body',
            attributes: createHibernateProxyCollection(),
            addressees: createHibernateProxyCollection(),
            actions: createHibernateProxyCollection()
        );

        JpaEntry jpa = mapper.map(dto, JpaEntry.class);
        assertMatches(jpa, dto);
    }


    @Test
    public void testMapDTOEntry() {
        def jpa = new JpaEntry(
            id: 1,
            title: 'jpatitle',
            image: 'jpaimage',
            body: 'jpabody',
            attributes: [
                new JpaAttribute(name: 'name1', values: ['val1', 'val2']),
                new JpaAttribute(name: 'name2', values: ['jpa1', 'jpa2', 'jpa3'])
            ],
            addressees: [
                new JpaAddressee(name: 'user1', type: RecipientType.INDIVIDUAL, recipients: [
                    new JpaRecipient(id: 100, username: 'test-user1'),
                    new JpaRecipient(id: 101, username: 'test-user2')
                ]),
                new JpaAddressee(name: 'user2', type: RecipientType.GROUP, recipients: [
                    new JpaRecipient(id: 200, username: 'test-group1')
                ])
            ],
            actions: [
                new JpaAction(id: 1, label: 'label1', clazz: 'org.jasig.test.Class1'),
                new JpaAction(id: 2, label: 'label2', clazz: 'org.jasig.test.Class2')
            ]
        );

        EntryDTO dto = mapper.map(jpa, EntryDTO);
        assertMatches(jpa, dto);
    }


    private void assertMatches(JpaEntry jpa, EntryDTO dto) {
        assert dto.id == jpa.id;
        assert dto.title == jpa.title;
        assert dto.image == jpa.image;

        assertAllMatch(jpa.attributes, dto.attributes, { converted, original ->
            assertAttributeMapping(converted, original, jpa);
        });

        assertAllMatch(jpa.addressees, dto.addressees, { converted, original ->
            assertAddresseeMapping(converted, original, jpa);
        });

        def matchFn = { act1, act2 -> act1.id == act2.id; };
        def assertFn = { act1, act2 -> assertActionMapping(act1, act2); }
        assertAllMatch(jpa.actions, dto.actions, assertFn, matchFn);
    }


    private void assertAllMatch(def converted, def original, def assertFn, def matchFn = defaultCompareFn) {
        // if converting from a HibernateProxy, expect dest
        // to be null or an empty list (depending on the default
        // defined in the DTO object)
        if (converted instanceof HibernateProxy) {
            assert original == null || original.isEmpty();
            return;
        }

        if (original instanceof HibernateProxy) {
            assert converted == null || converted.isEmpty();
            return;
        }

        assert original.size() == converted.size();
        for (def originalItem : original) {
            boolean found = false;
            for (def convertedItem : converted) {
                if (matchFn(convertedItem, originalItem)) {
                    assertFn(convertedItem, originalItem);
                    found = true;
                }
            }

            assert found : "Item ${list1} not converted";
        }
    }


    private void assertAttributeMapping(JpaAttribute attr1, AttributeDTO attr2, JpaEntry entry) {
        assert attr1 != null;
        assert attr2 != null;
        assert attr1.entryId == entry.id;

        assert attr1.values.size() == attr2.values.size();
        assert attr1.values.containsAll(attr2.values);
    }


    private void assertAddresseeMapping(JpaAddressee addr1, AddresseeDTO addr2, JpaEntry entry) {
        assert addr1 != null;
        assert addr2 != null;
        assert addr1.entryId == entry.id;

        assert addr1.name == addr2.name;
        assert addr1.type == addr2.type;

        def matchFn = { r1, r2 -> r1.id == r2.id };
        def assertFn = { r1, r2 ->
            assertRecipientMapping(r1, r2, addr1);
        };
        assertAllMatch(addr1.recipients, addr2.recipients, assertFn, matchFn);
    }


    private void assertRecipientMapping(JpaRecipient recip1, RecipientDTO recip2, JpaAddressee addr) {
        assert recip1 != null;
        assert recip2 != null;
        assert recip1.addresseeId == addr.id;

        assert recip1.id == recip2.id;
        assert recip1.username == recip2.username;
    }


    private void assertEventMapping(JpaEvent event1, EventDTO event2, JpaRecipient recipient) {
        assert event1 != null;
        assert event2 != null;
        assert event1.recipientId == recipient.id;

        assert event1.id == event2.id;
        assert event1.timestamp == event2.timestamp;
        assert event1.state == event2.state;
    }


    private void assertActionMapping(JpaAction action1, ActionDTO action2) {
        assert action1 != null;
        assert action2 != null;

        assert action1.id == action2.id;
        assert action1.label == action2.label;
        assert action1.clazz == action2.clazz;
    }


    private <T> Set<T> createHibernateProxyCollection(Class<? extends Collection> colClass = Set.class) {
        // create a proxy of a set and a hibernate proxy that
        // always looks uninitialized.
        return new ProxyGenerator().instantiateAggregate([
            getHibernateLazyInitializer: { -> [
                isUninitialized: { -> return true; }
            ] as LazyInitializer; }
        ], [colClass, HibernateProxy]);
    }
}
