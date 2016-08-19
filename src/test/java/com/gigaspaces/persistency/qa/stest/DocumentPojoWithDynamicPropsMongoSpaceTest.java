package com.gigaspaces.persistency.qa.stest;


import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.persistency.metadata.MongoDocumentObjectConverter;
import com.gigaspaces.persistency.qa.model.TestDataTypeWithDynamicProps;
import com.gigaspaces.persistency.qa.model.TestDataTypeWithDynamicPropsDocument;
import com.gigaspaces.persistency.qa.model.TestDataTypeWithDynamicPropsPojo;
import com.gigaspaces.persistency.qa.model.TestDataTypeWithDynamicPropsUtils;
import junit.framework.Assert;

public class DocumentPojoWithDynamicPropsMongoSpaceTest extends
		AbstractSystemTestUnit {

	MongoDocumentObjectConverter converter = new MongoDocumentObjectConverter();

	@Override
	public void test() {
		testWritePojoReadPojo();
		testWriteDocumentReadPojo();
		testWriteDocumentReadDocument();
		testWritePojoReadDocument();

	}

	private void testWritePojoReadPojo() {
		test(new TestDescriptor() {
			@Override
            public TestDataTypeWithDynamicProps getWritten() {
				return new TestDataTypeWithDynamicPropsPojo();
			}

			@Override
            public TestDataTypeWithDynamicProps getTemplate() {
				return new TestDataTypeWithDynamicPropsPojo();
			}
		});
	}

	@Override
	protected String getPUJar() {
		return "/document-pojo-with-dynamic-properties.jar";
	}

	private void testWriteDocumentReadPojo() {
		test(new TestDescriptor() {
			@Override
            public TestDataTypeWithDynamicProps getWritten() {
				return new TestDataTypeWithDynamicPropsDocument();
			}

			@Override
            public TestDataTypeWithDynamicProps getTemplate() {
				return new TestDataTypeWithDynamicPropsPojo();
			}
		});
	}

	private void testWritePojoReadDocument() {
		test(new TestDescriptor() {
			@Override
            public TestDataTypeWithDynamicProps getWritten() {
				return new TestDataTypeWithDynamicPropsPojo();
			}

			@Override
            public TestDataTypeWithDynamicProps getTemplate() {
				return new TestDataTypeWithDynamicPropsDocument();
			}
		});
	}

	private void testWriteDocumentReadDocument() {
		test(new TestDescriptor() {
			@Override
            public TestDataTypeWithDynamicProps getWritten() {
				return new TestDataTypeWithDynamicPropsDocument();
			}

			@Override
            public TestDataTypeWithDynamicProps getTemplate() {
				return new TestDataTypeWithDynamicPropsPojo();
			}
		});
	}

	private void test(TestDescriptor descriptor) {
		TestDataTypeWithDynamicProps data = descriptor.getWritten();
		TestDataTypeWithDynamicPropsUtils.populateAllProperties(data);
		gigaSpace.write(data);
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
		// read multiple to ensure we hit monogo
		Object[] readDataArr = gigaSpace.readMultiple(descriptor.getTemplate());
		Object readData = readDataArr[0];
		TestDataTypeWithDynamicProps typedReadData = null;
		if (readData instanceof TestDataTypeWithDynamicProps)
			typedReadData = (TestDataTypeWithDynamicProps) readData;
		else
			// // this could only be SpaceDocument
			typedReadData = (TestDataTypeWithDynamicProps) converter
					.toObject((SpaceDocument) readData);

		TestDataTypeWithDynamicPropsUtils.assertTestDataEquals(data,
				typedReadData);
		gigaSpace.clear(descriptor.getTemplate());
		waitForEmptyReplicationBacklogAndClearMemory(gigaSpace);
		Assert.assertEquals("unexpected entries", 0,
				gigaSpace.count(descriptor.getTemplate()));
	}

	interface TestDescriptor {
		TestDataTypeWithDynamicProps getWritten();

		TestDataTypeWithDynamicProps getTemplate();
	}
}
