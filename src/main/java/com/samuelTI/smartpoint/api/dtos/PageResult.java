package com.samuelTI.smartpoint.api.dtos;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

	public <R> PageResult<R> map(Function<T, R> mapper) {
		List<R> mapped = content.stream().map(mapper).toList();
		return new PageResult<>(mapped, page, size, totalElements, totalPages);
	}

	public static <T> PageResult<T> of(List<T> allItems, int page, int size) {
		int total = allItems.size();
		int start = page * size;
		int end = Math.min(start + size, total);
		List<T> pageContent = start >= total ? List.of() : allItems.subList(start, end);
		int totalPages = (total + size - 1) / size;
		return new PageResult<>(pageContent, page, size, total, totalPages);
	}
}
