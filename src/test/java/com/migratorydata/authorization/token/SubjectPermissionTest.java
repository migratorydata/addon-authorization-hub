package com.migratorydata.authorization.token;

import com.migratorydata.authorization.common.token.Permissions;
import com.migratorydata.authorization.common.token.SubjectPermission;
import static com.migratorydata.authorization.common.token.SubjectPermission.SYMBOL_REGEX;
import org.junit.Assert;
import org.junit.Test;

public class SubjectPermissionTest {

    private SubjectPermission root = new SubjectPermission("");

    @Test
    public void test_root_wildcard() {
        root.setPermission("/*", Permissions.Permission.ALL);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );
    }

    @Test
    public void test_root_normal_subject() {
        root.setPermission("/a", Permissions.Permission.ALL);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.NONE);

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE);

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a");
        Assert.assertTrue(permission == Permissions.Permission.ALL );
    }

    @Test
    public void test_root_wildcard_root_normal_subject() {
        root.setPermission("/*", Permissions.Permission.ALL);
        root.setPermission("/a", Permissions.Permission.PUB);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/q");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/w/b");
        Assert.assertTrue(permission == Permissions.Permission.ALL );
    }

    @Test
    public void test_root_wildcard_normal_subject() {
        root.setPermission("/*", Permissions.Permission.ALL);
        root.setPermission("/a/b", Permissions.Permission.PUB);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/q");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/w/b");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a");
        Assert.assertTrue(permission == Permissions.Permission.ALL );
    }

    @Test
    public void test_normal_subject() {
        root.setPermission("/a/b/c/d", Permissions.Permission.ALL);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.NONE);

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE);

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a/b/c/d/e");
        Assert.assertTrue(permission == Permissions.Permission.NONE );
    }

    @Test
    public void test_more_normal_subject_asc() {
        root.setPermission("/a/b/c/d", Permissions.Permission.ALL);
        root.setPermission("/a/b", Permissions.Permission.SUB);
        root.setPermission("/a", Permissions.Permission.PUB);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE);

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

    }

    @Test
    public void test_more_normal_subject_dsc() {
        root.setPermission("/a", Permissions.Permission.PUB);
        root.setPermission("/a/b", Permissions.Permission.SUB);
        root.setPermission("/a/b/c/d", Permissions.Permission.ALL);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE);

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

    }

    @Test
    public void test_wildcard_subject() {
        root.setPermission("/q/w/*", Permissions.Permission.PUB);

        Permissions.Permission permission = root.getPermission("/q/w");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/q/w/a");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/q/w/c/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/q");
        Assert.assertTrue(permission == Permissions.Permission.NONE );
    }

    @Test
    public void test_normal_subject_on_top_wildcard_subject() {
        root.setPermission("/a/b/c/d", Permissions.Permission.ALL);
        root.setPermission("/a/b/*", Permissions.Permission.PUB);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );
    }

    @Test
    public void test_wildcard_subject_on_top_normal_subject() {
        root.setPermission("/a/b/*", Permissions.Permission.PUB);
        root.setPermission("/a/b/c/d", Permissions.Permission.ALL);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("a/b");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );
    }

    @Test
    public void test_wildcard_subject_multiple_patterns() {
        root.setPermission("/a/b/c/d", Permissions.Permission.ALL);
        root.setPermission("/q/w/*", Permissions.Permission.PUB);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/q/w/w/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/q/w/a");
        Assert.assertTrue(permission == Permissions.Permission.PUB );
    }

    @Test
    public void test_wildcard_char_middle() {
        root.setPermission("/a/b/c/d", Permissions.Permission.PUB);
        root.setPermission("/a/*/c/d", Permissions.Permission.ALL);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.ALL );

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.PUB );
    }

    @Test
    public void test_symbol_last() {
        root.setPermission("/a/b/(s)", Permissions.Permission.PUB);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b/c/d");
        Assert.assertTrue(permission == Permissions.Permission.NONE );
    }

    @Test
    public void test_symbol_middle() {
        root.setPermission("/a/(s)/b", Permissions.Permission.PUB);
        root.setPermission("/a/c/b", Permissions.Permission.SUB);
        root.setPermission("/a/c/d", Permissions.Permission.ALL);


        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/c/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/x/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/c/d");
        Assert.assertTrue(permission == Permissions.Permission.ALL );
    }

    @Test
    public void test_multiple_symbols_middle() {
        root.setPermission("/a/(s)/b", Permissions.Permission.ALL);
        root.setPermission("/a/(s)/(s)/b", Permissions.Permission.SUB);
        root.setPermission("/a/c/b", Permissions.Permission.PUB);
        root.setPermission("/a/c/d", Permissions.Permission.PUB);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/c/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b/d");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/d/c");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/b/d/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/d/b/e");
        Assert.assertTrue(permission == Permissions.Permission.NONE );

        permission = root.getPermission("/a/x/x/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );
    }

    @Test
    public void test_multiple_symbols_middle_wildcard() {
        root.setPermission("/a/(s)/b/(s)", Permissions.Permission.PUB);
        root.setPermission("/a/(s)/b", Permissions.Permission.ALL);
        root.setPermission("/a/(s)/(s)/b", Permissions.Permission.SUB);
        root.setPermission("/a/c/b", Permissions.Permission.PUB);
        root.setPermission("/a/c/d", Permissions.Permission.PUB);
        root.setPermission("/a/*", Permissions.Permission.SUB);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/c/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b/d");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/d/c");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/d/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/x/b/y");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

    }

    @Test
    public void test_multiple_symbols_middle_wildcard_reverse() {
        root.setPermission("/a/*", Permissions.Permission.SUB);
        root.setPermission("/a/c/d", Permissions.Permission.PUB);
        root.setPermission("/a/c/b", Permissions.Permission.PUB);
        root.setPermission("/a/(s)/(s)/b", Permissions.Permission.SUB);
        root.setPermission("/a/(s)/b", Permissions.Permission.ALL);
        root.setPermission("/a/(s)/b/(s)", Permissions.Permission.PUB);

        System.out.println(root);

        Permissions.Permission permission = root.getPermission("/a/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/c");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/c/b");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

        permission = root.getPermission("/a/b/d");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/d/c");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/b/d/b");
        Assert.assertTrue(permission == Permissions.Permission.SUB );

        permission = root.getPermission("/a/x/b/y");
        Assert.assertTrue(permission == Permissions.Permission.PUB );

    }

    @Test
    public void testSymbolRegex() {
        Assert.assertTrue("(a)".matches(SYMBOL_REGEX));
        Assert.assertFalse("(a".matches(SYMBOL_REGEX));
    }
}
