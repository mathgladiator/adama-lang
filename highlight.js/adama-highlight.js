hljs.registerLanguage("adama", (() => {
    "use strict";

    const IDENT_RE = '[A-Za-z$_][0-9A-Za-z_]*';
    const KEYWORD_LIST = [
        "enum", "channel", "dispatch", "record", "message", "rpc",
        "service",
        "function", "procedure", "test",
        "import", "view", "bubble", "policy",
        "require", "index", "method",
        "while", "do", "for", "foreach", "if", "break", "continue", "block", "abort", "return",
        "transition", "invoke", "preempt", "assert",
        "iterate", "where", "where_as", "order", "shuffle", "limit", "reduce", "offset", "asc", "desc", "via", "materialize", "unique", "rank", "traverse",
        "required", "lossy",
        "get", "put", "delete", "options",
        "daily", "monthly", "hourly", "weekly",
        "requires",
        "create", "send", "invent", "maximum_history", // TODO: scope within @static if possible
        "auto", "var", "let", "formula", "readonly",
        "public", "private", "viewer_is", "use_policy", "use", "filter"];

    const LITERALS = [
      "true",
      "false",
      "@no_one",
      "@i",
      "@nothing",
      "@null",
      "@blocked",
      "@stable",
      "@null",
      "@context",
      "@headers",
      "@parameters",
      "@viewer",
      "@self",
      "@who",
      "@maybe"
    ];

    const BUILT_IN = [
        "bool",
        "int",
        "double",
        "client",
        "asset",
        "dynamic",
        "complex",
        "long",
        "string",
        "date",
        "datetime",
        "time",
        "timespan",
        "label",
        "map",  // <,>
        "table", // <
        "channel", // <
        "list", // <
        "maybe",  // <
        "future" // <
    ];

    const BUILT_IN_VARIABLES = [];

    const KEYWORDS = {
        $pattern: IDENT_RE,
        keyword: KEYWORD_LIST,
        literal: LITERALS,
        built_in: BUILT_IN,
        "variable.language": BUILT_IN_VARIABLES
    };

    return e => ({
        aliases: ["a", "adama"],
        keywords: KEYWORDS,
        contains: [
            {
                className: 'number',
                begin: '#' + IDENT_RE
            },
            {
                className: 'keyword',
                begin: '@' + IDENT_RE
            },
            e.C_LINE_COMMENT_MODE,
            e.C_BLOCK_COMMENT_MODE,
            e.C_NUMBER_MODE,
            e.QUOTE_STRING_MODE]
    })
})());