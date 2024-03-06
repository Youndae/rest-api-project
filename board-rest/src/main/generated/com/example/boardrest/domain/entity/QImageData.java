package com.example.boardrest.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QImageData is a Querydsl query type for ImageData
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QImageData extends EntityPathBase<ImageData> {

    private static final long serialVersionUID = 1598866751L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QImageData imageData = new QImageData("imageData");

    public final QImageBoard imageBoard;

    public final StringPath imageName = createString("imageName");

    public final NumberPath<Integer> imageStep = createNumber("imageStep", Integer.class);

    public final StringPath oldName = createString("oldName");

    public QImageData(String variable) {
        this(ImageData.class, forVariable(variable), INITS);
    }

    public QImageData(Path<? extends ImageData> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QImageData(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QImageData(PathMetadata metadata, PathInits inits) {
        this(ImageData.class, metadata, inits);
    }

    public QImageData(Class<? extends ImageData> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.imageBoard = inits.isInitialized("imageBoard") ? new QImageBoard(forProperty("imageBoard"), inits.get("imageBoard")) : null;
    }

}

