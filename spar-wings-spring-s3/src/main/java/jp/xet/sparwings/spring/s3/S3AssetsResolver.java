package jp.xet.sparwings.spring.s3;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @author fd0
 */
public class S3AssetsResolver extends AbstractResourceResolver {
	
	/**
	 * resource loader from s3
	 */
	private S3ObjectResourceLoader s3ObjectResourceLoader;
	
	/**
	 * bucket name for assets
	 */
	private String assetsBucket;
	
	/**
	 * prefix in assetsBucket
	 */
	private String prefix;
	
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param s3ObjectResourceLoader
	 * @param assetsBucket
	 * @param prefix
	 * @since #version#
	 */
	public S3AssetsResolver(S3ObjectResourceLoader s3ObjectResourceLoader, String assetsBucket, String prefix) {
		this.s3ObjectResourceLoader = s3ObjectResourceLoader;
		this.assetsBucket = assetsBucket;
		this.prefix = prefix;
	}
	
	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
			List<? extends Resource> locations, ResourceResolverChain chain) {
		return resolveResourceInternal(requestPath);
	}
	
	/**
	 * Build S3 URI (ex. s3://bucket/prefix/requestPath).
	 * 
	 * @param requestPath
	 * @return Uri for s3
	 */
	public String buildS3Uri(String requestPath) {
		UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
		uriComponentsBuilder.scheme("s3");
		uriComponentsBuilder.host(assetsBucket);
		if (!prefix.isEmpty()) {
			uriComponentsBuilder.path(prefix);
		}
		uriComponentsBuilder.pathSegment(requestPath);
		return uriComponentsBuilder.toUriString();
	}
	
	/**
	 * Resolve resource.
	 * 
	 * @param requestPath
	 * @return resource from s3
	 */
	public Resource resolveResourceInternal(String requestPath) {
		Resource resource = s3ObjectResourceLoader.getResource(buildS3Uri(requestPath));
		if (!resource.exists()) {
			return null; // returns 404;
		}
		return resource;
	}
	
	@Override
	protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations,
			ResourceResolverChain chain) {
		return chain.resolveUrlPath(resourceUrlPath, locations);
	}
	
}
