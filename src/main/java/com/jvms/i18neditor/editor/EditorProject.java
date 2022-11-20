package com.jvms.i18neditor.editor;

import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.jvms.i18neditor.FileStructure;
import com.jvms.i18neditor.Resource;
import com.jvms.i18neditor.ResourceType;

/**
 * This class represents an editor project.
 * 
 * @author Jacob van Mourik
 */
public class EditorProject {	
	private Path path;
	private String resourceFileDefinition;
	private ResourceType resourceType;
	private List<Resource> resources;
	private boolean minifyResources;
	private boolean flattenJSON;
	private boolean useUTF8Encoding = true;
	private boolean useLatin1Encoding = false;
	private FileStructure resourceFileStructure;
	
	public EditorProject(Path path) {
		this.path = path;
		this.resources = Lists.newLinkedList();
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	public List<Resource> getResources() {
		return ImmutableList.copyOf(resources);
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}
	
	public void addResource(Resource resource) {
		resources.add(resource);
	}
	
	public boolean hasResources() {
		return !resources.isEmpty();
	}
	
	public String getResourceFileDefinition() {
		return resourceFileDefinition;
	}

	public void setResourceFileDefinition(String resourceFileDefinition) {
		this.resourceFileDefinition = resourceFileDefinition;
	}

	public boolean isMinifyResources() {
		return minifyResources;
	}

	public void setMinifyResources(boolean minifyResources) {
		this.minifyResources = minifyResources;
	}

	public boolean isFlattenJSON() {
		return flattenJSON;
	}

	public void setFlattenJSON(boolean flattenJSON) {
		this.flattenJSON = flattenJSON;
	}

	public boolean isUseUTF8Encoding() {
		return useUTF8Encoding;
	}

	public void setUseUTF8Encoding(boolean useUTF8Encoding) {
		this.useUTF8Encoding = useUTF8Encoding;
	}

	public boolean isUseLatin1Encoding() {
		return useLatin1Encoding;
	}

	public void setUseLatin1Encoding(boolean useLatin1Encoding) {
		this.useLatin1Encoding = useLatin1Encoding;
	}

	public boolean supportsResourceParentValues() {
		return resourceType == ResourceType.Properties;
	}

	public FileStructure getResourceFileStructure() {
		return resourceFileStructure;
	}

	public void setResourceFileStructure(FileStructure resourceFileStructure) {
		this.resourceFileStructure = resourceFileStructure;
	}
}
