/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.xet.sparwings.common.filters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.google.common.base.Strings;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Servlet filter implementation to compress response HTML.
 * 
 * @since 0.7
 * @version $Id$
 * @author daisuke
 */
public class HtmlCompressionFilter extends OncePerRequestFilter {
	
	private boolean enabled = true;
	
	// default settings
	private boolean removeComments = true;
	
	private boolean removeMultiSpaces = true;
	
	// optional settings
	private boolean removeIntertagSpaces = false;
	
	private boolean removeQuotes = false;
	
	private boolean compressJavaScript = false;
	
	private boolean compressCss = false;
	
	// YUICompressor settings
	private boolean yuiJsNoMunge = false;
	
	private boolean yuiJsPreserveAllSemiColons = false;
	
	private boolean yuiJsDisableOptimizations = false;
	
	private int yuiJsLineBreak = -1;
	
	private int yuiCssLineBreak = -1;
	
	
	/**
	 * @param compressCss set <code>true</code> to enable CSS compression.
	 * Default is <code>false</code>
	 * @see HtmlCompressor#setCompressCss(boolean)
	 */
	public void setCompressCss(boolean compressCss) {
		this.compressCss = compressCss;
	}
	
	/**
	 * @param compressJavaScript set <code>true</code> to enable JavaScript compression.
	 * Default is <code>false</code>
	 * @see HtmlCompressor#setCompressJavaScript(boolean)
	 */
	public void setCompressJavaScript(boolean compressJavaScript) {
		this.compressJavaScript = compressJavaScript;
	}
	
	/**
	 * @param enabled set <code>false</code> to bypass all compression
	 * @see HtmlCompressor#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * @param removeComments set <code>true</code> to remove all HTML comments
	 * @see HtmlCompressor#setRemoveComments(boolean)
	 */
	public void setRemoveComments(boolean removeComments) {
		this.removeComments = removeComments;
	}
	
	/**
	 * @param removeIntertagSpaces set <code>true</code> to remove all inter-tag whitespace characters
	 * @see HtmlCompressor#setRemoveIntertagSpaces(boolean)
	 */
	public void setRemoveIntertagSpaces(boolean removeIntertagSpaces) {
		this.removeIntertagSpaces = removeIntertagSpaces;
	}
	
	/**
	 * @param removeMultiSpaces set <code>true</code> to replace all multiple whitespace characters
	 * will single spaces.
	 * @see HtmlCompressor#setRemoveMultiSpaces(boolean)
	 */
	public void setRemoveMultiSpaces(boolean removeMultiSpaces) {
		this.removeMultiSpaces = removeMultiSpaces;
	}
	
	/**
	 * @param removeQuotes set <code>true</code> to remove unnecessary quotes from tag attributes
	 * @see HtmlCompressor#setRemoveQuotes(boolean)
	 */
	public void setRemoveQuotes(boolean removeQuotes) {
		this.removeQuotes = removeQuotes;
	}
	
	/**
	 * @param yuiCssLineBreak set number of symbols per line
	 * @see HtmlCompressor#setYuiCssLineBreak(int)
	 */
	public void setYuiCssLineBreak(int yuiCssLineBreak) {
		this.yuiCssLineBreak = yuiCssLineBreak;
	}
	
	/**
	 * @param yuiJsDisableOptimizations set <code>true<code> to enable
	 * <code>disable-optimizations</code> mode
	 * @see HtmlCompressor#setYuiJsDisableOptimizations(boolean)
	 */
	public void setYuiJsDisableOptimizations(boolean yuiJsDisableOptimizations) {
		this.yuiJsDisableOptimizations = yuiJsDisableOptimizations;
	}
	
	/**
	 * @param yuiJsLineBreak set number of symbols per line
	 * @see HtmlCompressor#setYuiJsLineBreak(int)
	 */
	public void setYuiJsLineBreak(int yuiJsLineBreak) {
		this.yuiJsLineBreak = yuiJsLineBreak;
	}
	
	/**
	 * @param yuiJsNoMunge set <code>true</code> to enable <code>nomunge</code> mode
	 * @see HtmlCompressor#setYuiJsNoMunge(boolean)
	 */
	public void setYuiJsNoMunge(boolean yuiJsNoMunge) {
		this.yuiJsNoMunge = yuiJsNoMunge;
	}
	
	/**
	 * @param yuiJsPreserveAllSemiColons set <code>true<code> to enable <code>preserve-semi</code> mode
	 * @see HtmlCompressor#setYuiJsPreserveAllSemiColons(boolean)
	 */
	public void setYuiJsPreserveAllSemiColons(boolean yuiJsPreserveAllSemiColons) {
		this.yuiJsPreserveAllSemiColons = yuiJsPreserveAllSemiColons;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		OutputStreamResponseWrapper wrappedResponse = new OutputStreamResponseWrapper(response);
		
		filterChain.doFilter(request, wrappedResponse);
		
		ByteArrayOutputStream baos = wrappedResponse.getRealOutputStream();
		
		if (enabled && Strings.nullToEmpty(response.getContentType()).startsWith("text/html")) {
			HtmlCompressor compressor = new HtmlCompressor();
			compressor.setEnabled(enabled);
			compressor.setRemoveComments(removeComments);
			compressor.setRemoveMultiSpaces(removeMultiSpaces);
			compressor.setRemoveIntertagSpaces(removeIntertagSpaces);
			compressor.setRemoveQuotes(removeQuotes);
			compressor.setCompressJavaScript(compressJavaScript);
			compressor.setCompressCss(compressCss);
			compressor.setYuiJsNoMunge(yuiJsNoMunge);
			compressor.setYuiJsPreserveAllSemiColons(yuiJsPreserveAllSemiColons);
			compressor.setYuiJsDisableOptimizations(yuiJsDisableOptimizations);
			compressor.setYuiJsLineBreak(yuiJsLineBreak);
			compressor.setYuiCssLineBreak(yuiCssLineBreak);
			
			try (PrintWriter writer = response.getWriter()) {
				String compressed = compressor.compress(baos.toString());
				response.setContentLength(compressed.length());
				writer.write(compressed);
			}
		} else if (baos != null) {
			try (ServletOutputStream outputStream = response.getOutputStream()) {
				outputStream.write(baos.toByteArray());
			}
		}
	}
	
	
	static class OutputStreamResponseWrapper extends HttpServletResponseWrapper {
		
		private ByteArrayOutputStream realOutputStream;
		
		private ServletOutputStream stream;
		
		private PrintWriter writer;
		
		
		public OutputStreamResponseWrapper(HttpServletResponse response) {
			super(response);
		}
		
		public ServletOutputStream createOutputStream() {
			realOutputStream = new ByteArrayOutputStream();
			return new ServletOutputStreamWrapper(realOutputStream);
		}
		
		public void finishResponse() {
			try {
				if (writer != null) {
					writer.close();
				} else if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				// ignore
			}
		}
		
		@Override
		public void flushBuffer() throws IOException {
			stream.flush();
		}
		
		@Override
		public ServletOutputStream getOutputStream() {
			if (writer != null) {
				throw new IllegalStateException("getWriter() has already been called!");
			}
			
			if (stream == null) {
				stream = createOutputStream();
			}
			return stream;
		}
		
		/**
		 * Gets the underlying instance of the output stream.
		 * 
		 * @return underlying instance of the output stream
		 */
		public ByteArrayOutputStream getRealOutputStream() {
			return realOutputStream;
		}
		
		@Override
		public PrintWriter getWriter() throws IOException {
			if (writer != null) {
				return writer;
			}
			
			if (stream != null) {
				throw new IllegalStateException("getOutputStream() has already been called!");
			}
			
			stream = createOutputStream();
			writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
			return writer;
		}
		
		@Override
		public void setContentLength(int length) {
		}
	}
	
	/**
	 * A wrapper to provide a concrete implementation of the servlet output stream, so we can wrap other streams.
	 * Such as in a filter wrapping a servlet response.
	 *
	 * @author thein
	 * @see <a href="http://www.java-forums.org/java-servlet/20631-how-get-content-httpservletresponse.html"></a>
	 */
	static class ServletOutputStreamWrapper extends ServletOutputStream {
		
		OutputStream out;
		
		boolean closed = false;
		
		
		public ServletOutputStreamWrapper(OutputStream realStream) {
			out = realStream;
		}
		
		@Override
		public void close() throws IOException {
			if (closed) {
				throw new IOException("This output stream has already been closed");
			}
			out.flush();
			out.close();
			
			closed = true;
		}
		
		@Override
		public void flush() throws IOException {
			if (closed) {
				throw new IOException("Cannot flush a closed output stream");
			}
			out.flush();
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			write(b, 0, b.length);
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			// System.out.println("writing...");
			if (closed) {
				throw new IOException("Cannot write to a closed output stream");
			}
			out.write(b, off, len);
		}
		
		@Override
		public void write(int b) throws IOException {
			if (closed) {
				throw new IOException("Cannot write to a closed output stream");
			}
			out.write((byte) b);
		}
		
		@Override
		public boolean isReady() {
			return true == false;
		}
		
		@Override
		public void setWriteListener(WriteListener writeListener) {
		}
	}
}
