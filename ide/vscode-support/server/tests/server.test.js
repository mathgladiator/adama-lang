const assert = require('chai').assert;
const sinon = require('sinon');
const server = require('./server');

let sandbox;

beforeEach(() => {
    sandbox = sinon.createSandbox();
});

afterEach(() => {
    sandbox.restore();
});

describe('Language Server Tests', () => {
    
    it('should initialize with proposed features', () => {
        // Ensure correct server connection initialization
        assert.isDefined(server.connection);
    });

    it('should validate document on content change', () => {
        // Trigger and check document validation upon changes
        const doc = { uri: 'mockURI', getText: () => 'TEST' };
        const spy = sandbox.spy(server, 'validateTextDocument');
        server.documents.onDidChangeContent({ document: doc });
        assert.isTrue(spy.calledOnceWith(doc));
    });

    it('should detect uppercase words in document', async () => {
        // Check uppercase word detection
        const mockDocument = { uri: 'mockURI', getText: () => 'This is a TEST' };
        const diagnostics = await server.validateTextDocument(mockDocument);
        assert.equal(diagnostics[0].message, 'TEST is all uppercase.');
    });

    it('should limit diagnostics based on max problems setting', async () => {
        // Ensure diagnostics respect the set limit
        const mockDocument = { uri: 'mockURI', getText: () => 'TEST CASE WITH MULTIPLE UPPERCASE WORDS' };
        const mockSettings = { maxNumberOfProblems: 2 };
        sandbox.stub(server, 'getDocumentSettings').returns(Promise.resolve(mockSettings));
        const diagnostics = await server.validateTextDocument(mockDocument);
        assert.lengthOf(diagnostics, 2);
    });

    it('should log on watched file change', () => {
        // Check logging mechanism on file changes
        const logSpy = sandbox.spy(server.connection.console, 'log');
        server.connection.onDidChangeWatchedFiles({ changes: [] });
        assert.isTrue(logSpy.calledWith('We received an file change event'));
    });

    it('should provide expected completion items', () => {
        // Verify server's code completion items
        const items = server.connection.onCompletion({ position: {}, textDocument: {} });
        assert.deepInclude(items, { label: 'TypeScript', kind: server.node_1.CompletionItemKind.Text, data: 1 });
    });

    it('should resolve TypeScript completion with details', () => {
        // Verify detail addition for TypeScript completion
        const resolvedItem = server.connection.onCompletionResolve({ label: 'TypeScript', kind: server.node_1.CompletionItemKind.Text, data: 1 });
        assert.equal(resolvedItem.detail, 'TypeScript details');
    });


});
