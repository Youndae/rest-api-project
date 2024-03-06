package com.example.boardrest.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHierarchicalBoard is a Querydsl query type for HierarchicalBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHierarchicalBoard extends EntityPathBase<HierarchicalBoard> {

    private static final long serialVersionUID = -1302302601L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHierarchicalBoard hierarchicalBoard = new QHierarchicalBoard("hierarchicalBoard");

    public final StringPath boardContent = createString("boardContent");

    public final DateTimePath<java.util.Date> boardDate = createDateTime("boardDate", java.util.Date.class);

    public final NumberPath<Long> boardGroupNo = createNumber("boardGroupNo", Long.class);

    public final NumberPath<Integer> boardIndent = createNumber("boardIndent", Integer.class);

    public final NumberPath<Long> boardNo = createNumber("boardNo", Long.class);

    public final StringPath boardTitle = createString("boardTitle");

    public final StringPath boardUpperNo = createString("boardUpperNo");

    public final QMember member;

    public QHierarchicalBoard(String variable) {
        this(HierarchicalBoard.class, forVariable(variable), INITS);
    }

    public QHierarchicalBoard(Path<? extends HierarchicalBoard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHierarchicalBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHierarchicalBoard(PathMetadata metadata, PathInits inits) {
        this(HierarchicalBoard.class, metadata, inits);
    }

    public QHierarchicalBoard(Class<? extends HierarchicalBoard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

