import xml.sax

class ErrorHandler(xml.sax.ContentHandler):
    def __init__(self):
        self.locator = None

    def setDocumentLocator(self, locator):
        self.locator = locator

    def startElement(self, name, attrs):
        pass

    def endElement(self, name):
        pass

try:
    parser = xml.sax.make_parser()
    parser.setContentHandler(ErrorHandler())
    parser.parse(open("c:\\PROGETTI\\smartdoc-backend\\src\\main\\resources\\config\\query.xml", "r"))
    print("XML is valid.")
except xml.sax.SAXParseException as e:
    print(f"Error at line {e.getLineNumber()}, column {e.getColumnNumber()}: {e.getMessage()}")
except Exception as e:
    print(f"General error: {e}")
