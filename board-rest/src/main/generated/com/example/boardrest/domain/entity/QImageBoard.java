package com.example.boardrest.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QImageBoard is a Querydsl query type for ImageBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImageBoard extends EntityPathBase<ImageBoard> {

    private static final long serialVersionUID = -1976185871L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QImageBoard imageBoard = new QImageBoard("imageBoard");

    public final StringPath imageContent = createString("imageContent");

    public final SetPath<ImageData, QImageData> imageDataSet = this.<ImageData, QImageData>createSet("imageDataSet", ImageData.class, QImageData.class, PathInits.DIRECT2);

    public final DateTimePath<java.util.Date> imageDate = createDateTime("imageDate", java.util.Date.class);

    public final NumberPath<Long> imageNo = createNumber("imageNo", Long.class);

    public final StringPath imageTitle = createString("imageTitle");

    public final QMember member;

    public QImageBoard(String variable) {
        this(ImageBoard.class, forVariable(variable), INITS);
    }

    public QImageBoard(Path<? extends ImageBoard> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QImageBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QImageBoard(PathMetadata metadata, PathInits inits) {
        this(ImageBoard.class, metadata, inits);
    }

    public QImageBoard(Class<? extends ImageBoard> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

