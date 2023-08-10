package beanvest.processor.processingv2;

import beanvest.journal.entity.Entity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTest {
    @Test
    void groupDoesContain() {
        assertContains("G/a", "G/a");
        assertContains("G/a", "G/a:b");
        assertContains("G/a", "A/a:c");
        assertContains("G/a", "A/a:c:d");
        assertContains("G/a", "H/a:b:x");
        assertContains("G/a", "H/a:b:c:x");
        assertContains("G/a", "H/a:b:y");
        assertContains("G/a", "C/a:b:cash");
    }

    @Test
    void accountDoesContain() {
        assertContains("A/a", "A/a");
        assertContains("A/a", "H/a:x");
        assertContains("A/a:b", "H/a:b:x");
        assertContains("A/a:b", "C/a:b:cash");
        assertContains("A/a", "C/a:cash");

        assertNotContains("A/a", "A/a:b");
        assertNotContains("A/a", "A/a:b:c");
    }

    @Test
    void cashContains() {
        assertContains("C/a:c", "C/a:c");

        assertNotContains("C/a:c", "A/a");
        assertNotContains("C/a:c", "A/a:b");
    }

    @Test
    void groupDoesNotContain() {
        assertNotContains("G/a", "G/b");
        assertNotContains("G/a", "G/b:c");
        assertNotContains("G/a", "A/b:c");
        assertNotContains("G/a", "A/b:c:d");
        assertNotContains("G/a", "H/b:b:x");
        assertNotContains("G/a:c", "H/a:b:c:x");
        assertNotContains("G/b", "C/a:b:cash");
    }

    private void assertContains(String s, String s1) {
        var entity = Entity.fromStringId(s);
        var entity2 = Entity.fromStringId(s1);
        assertThat(entity.contains(entity2)).as(entity.stringId() + " should contain " + entity2.stringId()).isTrue();
    }

    private void assertNotContains(String s, String s1) {
        var entity = Entity.fromStringId(s);
        var entity2 = Entity.fromStringId(s1);
        assertThat(entity.contains(entity2)).as(entity.stringId() + " should NOT contain " + entity2.stringId()).isFalse();
    }
}