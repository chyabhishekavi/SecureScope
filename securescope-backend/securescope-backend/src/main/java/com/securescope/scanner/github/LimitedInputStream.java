package com.securescope.scanner.github;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class LimitedInputStream extends FilterInputStream {

	private final long maxBytes;
	private long bytesRead;

	LimitedInputStream(InputStream inputStream, long maxBytes) {
		super(inputStream);
		this.maxBytes = maxBytes;
	}

	@Override
	public int read() throws IOException {
		int value = super.read();
		if (value != -1) {
			countBytes(1);
		}
		return value;
	}

	@Override
	public int read(byte[] buffer, int offset, int length) throws IOException {
		int read = super.read(buffer, offset, length);
		if (read > 0) {
			countBytes(read);
		}
		return read;
	}

	private void countBytes(long count) throws IOException {
		bytesRead += count;
		if (bytesRead > maxBytes) {
			throw new IOException("Repository ZIP is too large");
		}
	}
}
