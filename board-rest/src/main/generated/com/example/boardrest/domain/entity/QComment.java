package com.example.boardrest.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = 2030070393L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final StringPath commentContent = createString("commentContent");

    public final DateTimePath<java.util.Date> commentDate = createDateTime("commentDate", java.util.Date.class);

    public final NumberPath<Long> commentGroupNo = createNumber("commentGroupNo", Long.class);

    public final NumberPath<Integer> commentIndent = createNumber("commentIndent", Integer.class);

    public final NumberPath<Long> commentNo = createNumber("commentNo", Long.class);

    public final NumberPath<Integer> commentStatus = createNumber("commentStatus", Integer.class);

    public final StringPath commentUpperNo = createString("commentUpperNo");

    public final QHierarchicalBoard hierarchicalBoard;

    public final QImageBoard imageBoard;

    public final QMember member;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.hierarchicalBoard = inits.isInitialized("hierarchicalBoard") ? new QHierarchicalBoard(forProperty("hierarchicalBoard"), inits.get("hierarchicalBoard")) : null;
        this.imageBoard = inits.isInitialized("imageBoard") ? new QImageBoard(forProperty("imageBoard"), inits.get("imageBoard")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

