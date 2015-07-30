public class PostsIndexHandlerTest {
    
    @Test
    public void emptyListIsHandledCorrectlyInHtmlOutput() {
        Model model = EasyMock.createMock(Model.class);
        expect(model.getAllPosts()).andReturn(Collections.EMPTY_LIST);
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "<body><h1>My wonderful blog</h1><div></div></body>";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), true));

        verify(model);
    }

    @Test
    public void aNonEmptyListIsHandledCorrectlyInHtmlOutput() {
        Model model = EasyMock.createMock(Model.class);

        Post post1 = new Post();
        post1.setTitle("First post");
        post1.setContent("First post content");
        post1.setCategories(ImmutableList.of("Howto", "BoringPosts"));

        Post post2 = new Post();
        post2.setTitle("Second post");
        post2.setContent("Second post content");
        post2.setCategories(ImmutableList.of());

        expect(model.getAllPosts()).andReturn(ImmutableList.of(post1, post2));
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "<body><h1>My wonderful blog</h1><div><div><h2>First post</h2><p>First post content</p><ul><li>Howto</li><li>BoringPosts</li></ul></div><div><h2>Second post</h2><p>Second post content</p><ul></ul></div></div></body>";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), true));

        verify(model);
    }

    @Test
    public void emptyListIsHandledCorrectlyInJsonOutput() {
        Model model = EasyMock.createMock(Model.class);
        expect(model.getAllPosts()).andReturn(Collections.EMPTY_LIST);
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "[ ]";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), false));

        verify(model);
    }

    @Test
    public void aNonEmptyListIsHandledCorrectlyInJsonOutput() {
        Model model = EasyMock.createMock(Model.class);

        Post post1 = new Post();
        post1.setTitle("First post");
        post1.setContent("First post content");
        post1.setCategories(ImmutableList.of("Howto", "BoringPosts"));

        Post post2 = new Post();
        post2.setTitle("Second post");
        post2.setContent("Second post content");
        post2.setCategories(ImmutableList.of());

        expect(model.getAllPosts()).andReturn(ImmutableList.of(post1, post2));
        replay(model);

        PostsIndexHandler handler = new PostsIndexHandler(model);
        String expectedHtml = "[ {\n" +
                "  \"post_uuid\" : null,\n" +
                "  \"title\" : \"First post\",\n" +
                "  \"content\" : \"First post content\",\n" +
                "  \"publishing_date\" : null,\n" +
                "  \"categories\" : [ \"Howto\", \"BoringPosts\" ]\n" +
                "}, {\n" +
                "  \"post_uuid\" : null,\n" +
                "  \"title\" : \"Second post\",\n" +
                "  \"content\" : \"Second post content\",\n" +
                "  \"publishing_date\" : null,\n" +
                "  \"categories\" : [ ]\n" +
                "} ]";
        assertEquals(new Answer(200, expectedHtml), handler.process(new EmptyPayload(), Collections.emptyMap(), false));

        verify(model);
    }

}